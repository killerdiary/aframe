package com.hy.frame.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.Nullable
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hy.frame.R
import com.hy.frame.bean.LoadCache
import com.hy.frame.mvp.IBasePresenter
import com.hy.frame.mvp.IBaseView
import com.hy.frame.ui.LoadingDialog
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.frame.util.MyToast
import com.hy.frame.util.StatusBarUtil

/**
 * 父类Activity
 * author HeYan
 * time 2015/12/23 16:40
 */
abstract class BaseActivity<out P : IBasePresenter> : AppCompatActivity(), android.view.View.OnClickListener, IBaseActivity, IBaseView {
    private var mApp: IBaseApplication? = null
    private var mContext: Context? = null
    private var lastSkipAct: String? = null //获取上一级的Activity名

    private var mToolbar: Toolbar? = null
    private var mFlyMain: FrameLayout? = null
    private var mLoadCache: LoadCache? = null
    private var mLoadingDialog: LoadingDialog? = null

    @Nullable
    private var mPresenter: P? = null//如果当前页面逻辑简单, Presenter 可以为 null

    protected fun getPresenter(): P? {
        if (mPresenter == null)
            mPresenter = buildPresenter()
        if (mPresenter != null)
            lifecycle.addObserver(mPresenter!!)
        return mPresenter
    }

    protected abstract fun buildPresenter(): P?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        if (isPermissionDenied()) {
            finish()
            return
        }
        initLayout()
        initView()
        initData()
    }

    private fun init() {
        lastSkipAct = intent.getStringExtra(LAST_ACT)// 获取上一级Activity的Name
        if (application !is IBaseApplication) {
            MyLog.e(javaClass, "BaseApplication Exception")
            System.exit(0)
            return
        }
        mApp = application as IBaseApplication
        mApp?.getActivityCache()?.add(this)
        mContext = this
    }

    override fun getCurContext(): Context = mContext!!

    override fun getCurApp(): IBaseApplication = mApp!!

    private fun initLayout() {
        if (isPortrait()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setContentView(if (isSingleLayout()) getLayoutId() else R.layout.v_base)
        mToolbar = findViewById(R.id.head_toolBar)
        mFlyMain = findViewById(R.id.base_flyMain)
        if (!isSingleLayout() && mFlyMain != null) {
            View.inflate(mContext, getLayoutId(), mFlyMain)
        }
        initToolbar()
    }


    private fun initToolbar() {
        if (mToolbar == null) return
        mToolbar!!.setTitle(R.string.empty)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val statusBarHeight = getStatusBarHeight()
            if (isTranslucentStatus() && statusBarHeight > 0) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                mToolbar!!.setPadding(0, statusBarHeight, 0, 0)
                if (mToolbar!!.layoutParams != null) {
                    val params = mToolbar!!.layoutParams
                    params.height = resources.getDimensionPixelSize(R.dimen.header_height) + statusBarHeight
                    mToolbar!!.layoutParams = params
                }
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }
        setSupportActionBar(mToolbar)
    }

    override fun getStatusBarHeight(): Int {
        return StatusBarUtil.getStatusBarHeight(mContext!!)
    }

    protected fun showNavigation(drawId: Int) {
        if (drawId > 0)
            mToolbar!!.setNavigationIcon(drawId)
        else
            mToolbar!!.navigationIcon = null
    }

    /**
     * 加载布局
     */
    protected open fun initLoadView(): Boolean {
        if (mFlyMain == null) {
            MyLog.e(javaClass, "Your layout must include 'FrameLayout',the ID must be 'base_flyMain'!")
            return false
        }
        if (mLoadCache != null) return true
        var loadView: View? = findViewById(R.id.base_llyLoad)
        //You need to add the layout
        if (loadView == null) {
            if (mFlyMain!!.childCount > 0) {
                loadView = View.inflate(mContext, R.layout.in_loading, null)
                mFlyMain!!.addView(loadView, 0)
            } else
                loadView = View.inflate(mContext, R.layout.in_loading, mFlyMain)
        }
        mLoadCache = LoadCache(loadView)
        return true
    }

    override fun showToast(msg: String?) {
        MyToast.show(mContext!!, msg)
    }

    override fun showLoading(resId: Int) {
        showLoading(getString(resId))
    }

    override fun showLoading(msg: String) {
        if (initLoadView()) {
            val count = mFlyMain!!.childCount
            for (i in 0 until count) {
                val v = mFlyMain!!.getChildAt(i)
                if (i > 0) v.visibility = View.GONE
            }
            mLoadCache!!.showLoading(msg)
        }
    }

    override fun showLoadingDialog(resId: Int) {
        showLoadingDialog(getString(resId))
    }

    override fun showLoadingDialog(msg: String) {
        if (mLoadingDialog == null)
            mLoadingDialog = LoadingDialog(mContext!!, msg)
        mLoadingDialog?.show()
    }

    override fun hideLoadingDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog?.dismiss()
    }

    override fun showNoData(resId: Int, drawId: Int) {
        showNoData(getString(resId), drawId)
    }

    override fun showNoData(msg: String, drawId: Int) {
        if (initLoadView()) {
            val count = mFlyMain!!.childCount
            for (i in 0 until count) {
                val v = mFlyMain!!.getChildAt(i)
                if (i > 0) v.visibility = View.GONE
            }
            mLoadCache!!.showNoData(msg, drawId)
        }
    }

    private var retry: Boolean = false//重试

    protected fun allowRetry() {
        if (mLoadCache != null) {
            retry = true
            mLoadCache!!.llyLoad!!.setOnClickListener(this)
        }
    }

    protected open fun onRetryRequest() {}

    /**
     * 显示内容View
     */
    override fun showCView() {
        if (initLoadView()) {
            val count = mFlyMain!!.childCount
            for (i in 0 until count) {
                val v = mFlyMain!!.getChildAt(i)
                if (i == 0)
                    v.visibility = View.GONE
                else
                    v.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 设置标题
     */
    override fun setTitle(@StringRes titleId: Int) {
        title = getString(titleId)

    }

    /**
     * 设置标题
     */
    override fun setTitle(title: CharSequence?) {
        if (mToolbar != null) {
            if (findViewById<View>(R.id.head_vTitle, mToolbar) == null)
                View.inflate(mContext, R.layout.in_head_title, mToolbar)
            findViewById<TextView>(R.id.head_vTitle, mToolbar)?.text = title
        }
    }

    override fun hideHeader() {
        if (mToolbar != null) mToolbar!!.visibility = View.GONE
    }

    @SuppressLint("ResourceType")
    override fun setHeaderLeft(@DrawableRes left: Int) {
        if (mToolbar != null && left > 0) {
            if (findViewById<View>(R.id.head_vLeft, mToolbar) == null)
                View.inflate(mContext, R.layout.in_head_left, mToolbar)
            val img = findViewById<ImageView>(R.id.head_vLeft, mToolbar)
            img?.setOnClickListener(this)
            img?.setImageResource(left)
        }
    }

    @SuppressLint("ResourceType")
    override fun setHeaderLeftTxt(@StringRes left: Int) {
        if (mToolbar != null && left > 0) {
            if (findViewById<View>(R.id.head_vLeft, mToolbar) == null)
                View.inflate(mContext, R.layout.in_head_tleft, mToolbar)
            val txt = findViewById<TextView>(R.id.head_vLeft, mToolbar)
            txt?.setOnClickListener(this)
            txt?.setText(left)
        }
    }

    @SuppressLint("ResourceType")
    override fun setHeaderRight(@DrawableRes right: Int) {
        addHeaderRight(right, null, R.id.head_vRight)
    }

    @SuppressLint("ResourceType")
    override fun setHeaderRightTxt(@StringRes right: Int) {
        addHeaderRight(right, getString(right), R.id.head_vRight)
    }

    override fun addHeaderRight(@DrawableRes right: Int, @IdRes id: Int) {
        addHeaderRight(right, null, id)
    }

    override fun addHeaderRightPath(@DrawableRes rightPath: String?, @IdRes id: Int) {
        addHeaderRight(0, rightPath, id)
    }

    @SuppressLint("ResourceType")
    private fun addHeaderRight(@DrawableRes right: Int, @DrawableRes rightPath: String?, @IdRes id: Int) {
        if (mToolbar != null) {
            var img: ImageView? = findViewById(id, mToolbar)
            if (img != null) {
                if (right == 0 && rightPath == null) {
                    mToolbar?.removeView(img)
                } else {
                    if (right != 0) {
                        img.setImageResource(right)
                    } else {
                        Glide.with(getCurContext()).asBitmap().apply(RequestOptions.noTransformation().placeholder(R.drawable.v_warn).error(R.drawable.v_warn)).load(rightPath).into(img)
                    }
                }
                return
            }
            if (right == 0 && rightPath == null) return
            val v = View.inflate(mContext, R.layout.in_head_right, null)
            img = findViewById(R.id.head_vRight, v)
            img?.id = id
            val array = theme.obtainStyledAttributes(intArrayOf(R.attr.appHeaderHeight))
            val width = array.getDimensionPixelSize(0, 0)
            array.recycle()
            val params = Toolbar.LayoutParams(width, width)
            //params.setMargins(0, 0, width * (rightCount - 1), 0);
            params.gravity = Gravity.RIGHT
            img?.layoutParams = params
            mToolbar!!.addView(v)
            img?.setOnClickListener(this)
            if (right != 0) {
                img?.setImageResource(right)
            } else {
                Glide.with(getCurContext()).asBitmap().apply(RequestOptions.noTransformation().placeholder(R.drawable.v_warn).error(R.drawable.v_warn)).load(rightPath).into(img!!)
            }
        }
    }

    /**
     * 头部
     */
    protected val header: View
        get() = mToolbar!!

    val headerHeight: Int
        get() {
            if (mToolbar != null) {
                return mToolbar!!.height
            }
            return 0
        }

    protected val headerTitle: View
        get() = findViewById(R.id.head_vTitle, mToolbar)!!

    protected val headerLeft: View
        get() = findViewById(R.id.head_vLeft, mToolbar)!!

    protected val headerRight: View
        get() = findViewById(R.id.head_vRight, mToolbar)!!

    protected val mainView: View
        get() = mFlyMain!!

    /**
     * 启动Activity
     */
    override fun startAct(cls: Class<*>, bundle: Bundle?, intent: Intent?) {
        var i = intent
        if (i == null)
            i = Intent()
        if (bundle != null)
            i.putExtra(BUNDLE, bundle)
        i.putExtra(LAST_ACT, this.javaClass.simpleName)
        i.setClass(this, cls)
        startActivity(i)
    }

//    /**
//     * 启动Activity，清空栈 并添加到栈顶，慎用
//     */
//    protected fun startActClear(cls: Class<*>, bundle: Bundle? = null, intent: Intent? = null) {
//        mApp?.getActivityCache()?.clear()
//        startAct(cls, bundle, intent)
//    }

    override fun startActForResult(cls: Class<*>, requestCode: Int, bundle: Bundle?, intent: Intent?) {
        var i = intent
        if (i == null)
            i = Intent()
        if (bundle != null)
            i.putExtra(BUNDLE, bundle)
        i.putExtra(LAST_ACT, this.javaClass.simpleName)
        i.setClass(this, cls)
        startActivityForResult(i, requestCode)
    }

    protected val bundle: Bundle?
        get() {
            if (intent.hasExtra(BUNDLE)) {
                return intent.getBundleExtra(BUNDLE)
            }
            return null
        }

    override fun finish() {
        mApp?.getActivityCache()?.remove(this)
        super.finish()
    }

    protected fun getStrings(vararg ids: Int): String {
        val sb = StringBuilder()
        for (id in ids) {
            sb.append(getString(id))
        }
        return sb.toString()
    }

    override fun onClick(v: View) {
        if (HyUtil.isFastClick)
            return
        when (v.id) {
            R.id.head_vLeft -> onLeftClick()
            R.id.head_vRight -> onRightClick()
            R.id.base_llyLoad -> onRetryRequest()
            else -> onViewClick(v)
        }
    }

    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    fun <T : View> findViewById(@IdRes id: Int, parent: View?): T? {
        return parent?.findViewById(id) ?: findViewById(id)
    }

    /**
     * 获取并绑定点击
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    protected fun <T : View> setOnClickListener(@IdRes id: Int, parent: View? = null): T? {
        val view = findViewById<T>(id, parent)
        view?.setOnClickListener(this) ?: return null
        return view
    }

    /**
     * 头-左边图标点击
     */
    override fun onLeftClick() {
        onBackPressed()
    }

    /**
     * 头-右边图标点击
     */
    override fun onRightClick() {}

    override fun onBackPressed() {
        finish()
    }

    companion object {
        const val LAST_ACT = "LAST_ACT"
        const val BUNDLE = "TAG_BUNDLE"
    }
}
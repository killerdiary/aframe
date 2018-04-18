package com.hy.frame.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.Nullable
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.hy.frame.R
import com.hy.frame.bean.LoadCache
import com.hy.frame.mvp.IBasePresenter
import com.hy.frame.mvp.IBaseView
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.frame.util.MyToast

/**
 * 父类Fragment
 * author HeYan
 * time 2015/12/23 17:12
 */
abstract class BaseFragment<P : IBasePresenter> : Fragment(), android.view.View.OnClickListener, IBaseFragment, IBaseView {

    private var mContentView: View? = null
    private var mToolbar: Toolbar? = null
    private var mFlyMain: FrameLayout? = null
    protected var mLoadCache: LoadCache? = null
    protected var mShowCount: Int = 0
    protected var mInit: Boolean = false

    @Nullable
    protected var mPresenter: P? = null//如果当前页面逻辑简单, Presenter 可以为 null

    override fun isTranslucentStatus(): Boolean {
        if (activity != null && activity is IBaseActivity) {
            val act = activity as IBaseActivity
            return act.isTranslucentStatus()
        }
        return false
    }

    override fun getFragment(): Fragment = this
    override fun getCurApp(): IBaseApplication {
        return (activity!! as IBaseView).getCurApp()
    }

    override fun getCurContext(): Context = context!!

    /**
     * 避免重复init
     */
    override fun onStart() {
        super.onStart()
        if (!mInit) {
            mInit = true
            initData()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // MyLog.d(getClass(), "onCreateView");
        mShowCount++
        if (mContentView == null) {
            val v: View? = inflater.inflate(if (isSingleLayout()) getLayoutId() else R.layout.act_base, container, false)
            mFlyMain = findViewById(R.id.base_flyMain, v)
            mToolbar = findViewById(R.id.head_toolBar, v)
            if (!isSingleLayout() && mFlyMain != null)
                View.inflate(context, getLayoutId(), mFlyMain)
            mContentView = v
            mInit = false
            initToolbar()
            initView()
        }
        return mContentView
    }

    private fun initToolbar() {
        if (mToolbar == null) return
        mToolbar!!.setTitle(R.string.empty)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val statusBarHeight = (activity as IBaseActivity).getStatusBarHeight()
            if (isTranslucentStatus() && statusBarHeight > 0) {
                mToolbar!!.setPadding(0, statusBarHeight, 0, 0)
                if (mToolbar!!.layoutParams != null) {
                    val params = mToolbar!!.layoutParams
                    params.height = resources.getDimensionPixelSize(R.dimen.header_height) + statusBarHeight
                    mToolbar!!.layoutParams = params
                }
            }
        }
    }

    /**
     * 加载布局
     */
    protected fun initLoadView(): Boolean {
        if (mFlyMain == null) {
            MyLog.e(javaClass, "Your layout must include 'FrameLayout',the ID must be 'base_flyMain'!")
            return false
        }
        if (mLoadCache != null) return true
        var loadView: View? = findViewById(R.id.base_llyLoad)
        //You need to add the layout
        if (loadView == null) {
            if (mFlyMain!!.childCount > 0) {
                loadView = View.inflate(context, R.layout.in_loading, null)
                mFlyMain!!.addView(loadView, 0)
            } else
                loadView = View.inflate(context, R.layout.in_loading, mFlyMain)
        }
        mLoadCache = LoadCache(loadView)
        return true
    }

    override fun showToast(msg: String?) {
        MyToast.show(context!!, msg)
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
        (activity!! as IBaseView).showLoadingDialog(msg)
    }

    override fun hideLoadingDialog() {
        (activity!! as IBaseView).hideLoadingDialog()
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
    protected fun setTitle(@StringRes titleId: Int) {
        setTitle(getString(titleId))
    }

    /**
     * 设置标题
     */
    protected fun setTitle(title: CharSequence?) {
        if (mToolbar != null) {
            if (findViewById<View>(R.id.head_vTitle, mToolbar) == null)
                View.inflate(getCurContext(), R.layout.in_head_title, mToolbar)
            findViewById<TextView>(R.id.head_vTitle, mToolbar)?.text = title
        }
    }

    protected fun hideHeader() {
        if (mToolbar != null) mToolbar!!.visibility = View.GONE
    }

    @SuppressLint("ResourceType")
    protected fun setHeaderLeft(@DrawableRes left: Int) {
        if (mToolbar != null && left > 0) {
            if (findViewById<View>(R.id.head_vLeft, mToolbar) == null)
                View.inflate(getCurContext(), R.layout.in_head_left, mToolbar)
            val img = findViewById<ImageView>(R.id.head_vLeft, mToolbar)
            img?.setOnClickListener(this)
            img?.setImageResource(left)
        }
    }

    @SuppressLint("ResourceType")
    protected fun setHeaderLeftTxt(@StringRes left: Int) {
        if (mToolbar != null && left > 0) {
            if (findViewById<View>(R.id.head_vLeft, mToolbar) == null)
                View.inflate(getCurContext(), R.layout.in_head_tleft, mToolbar)
            val txt = findViewById<TextView>(R.id.head_vLeft, mToolbar)
            txt?.setOnClickListener(this)
            txt?.setText(left)
        }
    }

    @SuppressLint("ResourceType")
    protected fun setHeaderRight(@DrawableRes right: Int) {
        if (mToolbar != null && right > 0) {
            if (findViewById<View>(R.id.head_vRight, mToolbar) == null)
                View.inflate(getCurContext(), R.layout.in_head_right, mToolbar)
            val img = findViewById<ImageView>(R.id.head_vRight, mToolbar)
            img?.setOnClickListener(this)
            img?.setImageResource(right)
        }
    }

    @SuppressLint("ResourceType")
    protected fun addHeaderRight(@DrawableRes right: Int, @IdRes id: Int) {
        if (mToolbar != null && right > 0) {
            val v = View.inflate(getCurContext(), R.layout.in_head_right, null)
            val img = findViewById<ImageView>(R.id.head_vRight, v)
            img?.id = id
            val array = activity!!.obtainStyledAttributes(intArrayOf(R.attr.appHeaderHeight))
            val width = array.getDimensionPixelSize(0, 0)
            array.recycle()
            val params = Toolbar.LayoutParams(width, width)
            //params.setMargins(0, 0, width * (rightCount - 1), 0);
            params.gravity = Gravity.RIGHT
            img?.layoutParams = params
            mToolbar!!.addView(v)
            img?.setOnClickListener(this)
            img?.setImageResource(right)
        }
    }

    @SuppressLint("ResourceType")
    protected fun setHeaderRightTxt(@StringRes right: Int) {
        if (mToolbar != null && right > 0) {
            if (findViewById<View>(R.id.head_vRight, mToolbar) == null)
                View.inflate(getCurContext(), R.layout.in_head_tright, mToolbar)
            val txt = findViewById<TextView>(R.id.head_vRight, mToolbar)
            txt?.setOnClickListener(this)
            txt?.setText(right)
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
            i.putExtra(BaseActivity.BUNDLE, bundle)
        i.putExtra(BaseActivity.LAST_ACT, this.javaClass.simpleName)
        i.setClass(activity, cls)
        startActivity(i)
    }

    override fun startActForResult(cls: Class<*>, requestCode: Int, bundle: Bundle?) {
        val i = Intent(activity, cls)
        i.putExtra(BaseActivity.LAST_ACT, this.javaClass.simpleName)
        if (bundle != null)
            i.putExtra(BaseActivity.BUNDLE, bundle)
        startActivityForResult(i, requestCode)
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
    fun <T : View> findViewById(@IdRes id: Int, parent: View? = null): T? {
        return parent?.findViewById(id) ?: mContentView?.findViewById(id)
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
    override fun onLeftClick() {}

    /**
     * 头-右边图标点击
     */
    override fun onRightClick() {}

    override fun onDestroy() {
        mPresenter?.onDestroy()
        super.onDestroy()
    }
}
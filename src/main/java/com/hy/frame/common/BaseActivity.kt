package com.hy.frame.common

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.hy.frame.R
import com.hy.frame.bean.LoadCache
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.http.MyHttpClient

/**
 * 父类Activity
 * author HeYan
 * time 2015/12/23 16:40
 */
abstract class BaseActivity : AppCompatActivity(), android.view.View.OnClickListener, IBaseActivity {
    protected var app: BaseApplication? = null
    protected var context: Context? = null
    private var lastAct: Class<*>? = null// 上一级 Activity
    var lastSkipAct: String? = null //获取上一级的Activity名
    private var toolbar: Toolbar? = null
    private var flyMain: FrameLayout? = null
    protected var loadCache: LoadCache? = null
    protected open var client: MyHttpClient? = null

    abstract fun isTranslucentStatus(): Boolean

    abstract fun isPermissionDenied(): Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initApp()
        if (isPermissionDenied()) {
            finish()
            return
        }
        init()
        initView()
        initData()
    }

    /**
     * 唯一布局ID
     */
    @LayoutRes
    protected abstract fun initSingleLayoutId(): Int

    private fun init() {
        context = this
        when {
            initSingleLayoutId() != 0 -> {
                setContentView(initSingleLayoutId())
                toolbar = findViewById(R.id.head_toolBar)
                flyMain = findViewById(R.id.base_flyMain)
            }
            initLayoutId() != 0 -> {
                setContentView(R.layout.act_base)
                toolbar = findViewById(R.id.head_toolBar)
                flyMain = findViewById(R.id.base_flyMain)
                View.inflate(context, initLayoutId(), flyMain)
            }
            else -> MyLog.e(javaClass, "initLayoutId not call")
        }
        initToolbar()
    }

    private fun initApp() {
        lastSkipAct = intent.getStringExtra(LAST_ACT)// 获取上一级Activity的Name
        try {
            app = application as BaseApplication
        } catch (e: Exception) {
            MyLog.e(javaClass, "BaseApplication Exception")
            System.exit(0)
            return
        }
        app!!.addActivity(this)
    }

    private fun initToolbar() {
        if (toolbar == null) return
        toolbar!!.title = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val statusBarHeight = statusBarHeight
            if (isTranslucentStatus() && statusBarHeight > 0) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                toolbar!!.setPadding(0, statusBarHeight, 0, 0)
                if (toolbar!!.layoutParams != null) {
                    val params = toolbar!!.layoutParams
                    params.height = resources.getDimensionPixelSize(R.dimen.header_height) + statusBarHeight
                    toolbar!!.layoutParams = params
                }
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }
        setSupportActionBar(toolbar)
    }

    val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    protected fun showNavigation(drawId: Int) {
        if (drawId > 0)
            toolbar!!.setNavigationIcon(drawId)
        else
            toolbar!!.navigationIcon = null
    }

    /**
     * 加载布局
     */
    protected open fun initLoadView(): Boolean {
        if (flyMain == null) {
            MyLog.e(javaClass, "Your layout must include 'FrameLayout',the ID must be 'base_flyMain'!")
            return false
        }
        if (loadCache != null) return true
        var loadView: View? = findViewById(R.id.base_llyLoad)
        //You need to add the layout
        if (loadView == null) {
            if (flyMain!!.childCount > 0) {
                loadView = View.inflate(context, R.layout.in_loading, null)
                flyMain!!.addView(loadView, 0)
            } else
                loadView = View.inflate(context, R.layout.in_loading, flyMain)
        }
        loadCache = LoadCache(loadView)
        return true
    }

    protected fun showLoading(msg: String = getString(R.string.loading)) {
        if (initLoadView()) {
            val count = flyMain!!.childCount
            for (i in 0 until count) {
                val v = flyMain!!.getChildAt(i)
                if (i > 0) v.visibility = View.GONE
            }
            loadCache!!.showLoading(msg)
        }
    }

    //R.drawable.img_hint_net_fail
    open fun showNoData(msg: String? = getString(R.string.hint_nodata), drawId: Int = R.mipmap.ic_nodata) {
        if (initLoadView()) {
            val count = flyMain!!.childCount
            for (i in 0 until count) {
                val v = flyMain!!.getChildAt(i)
                if (i > 0) v.visibility = View.GONE
            }
            loadCache!!.showNoData(msg, drawId)
        }
    }

    private var retry: Boolean = false//重试

    protected fun allowRetry() {
        if (loadCache != null) {
            retry = true
            loadCache!!.llyLoad!!.setOnClickListener(this)
        }
    }

    protected open fun onRetryRequest() {}

    /**
     * 显示内容View
     */
    protected fun showCView() {
        if (initLoadView()) {
            val count = flyMain!!.childCount
            for (i in 0 until count) {
                val v = flyMain!!.getChildAt(i)
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
        if (toolbar != null) {
            if (findViewById<View>(R.id.head_vTitle, toolbar) == null)
                View.inflate(context, R.layout.in_head_title, toolbar)
            findViewById<TextView>(R.id.head_vTitle, toolbar)?.text = title
        }
    }

    protected fun hideHeader() {
        if (toolbar != null) toolbar!!.visibility = View.GONE
    }

    protected fun setHeaderLeft(@DrawableRes left: Int) {
        if (toolbar != null && left > 0) {
            if (findViewById<View>(R.id.head_vLeft, toolbar) == null)
                View.inflate(context, R.layout.in_head_left, toolbar)
            val img = findViewById<ImageView>(R.id.head_vLeft, toolbar)
            img?.setOnClickListener(this)
            img?.setImageResource(left)
        }
    }

    protected fun setHeaderLeftTxt(@StringRes left: Int) {
        if (toolbar != null && left > 0) {
            if (findViewById<View>(R.id.head_vLeft, toolbar) == null)
                View.inflate(context, R.layout.in_head_tleft, toolbar)
            val txt = findViewById<TextView>(R.id.head_vLeft, toolbar)
            txt?.setOnClickListener(this)
            txt?.setText(left)
        }
    }

    protected fun setHeaderRight(@DrawableRes right: Int) {
        if (toolbar != null && right > 0) {
            if (findViewById<View>(R.id.head_vRight, toolbar) == null)
                View.inflate(context, R.layout.in_head_right, toolbar)
            val img = findViewById<ImageView>(R.id.head_vRight, toolbar)
            img?.setOnClickListener(this)
            img?.setImageResource(right)
        }
    }

    protected fun setHeaderRightTxt(@StringRes right: Int) {
        if (toolbar != null && right > 0) {
            if (findViewById<View>(R.id.head_vRight, toolbar) == null)
                View.inflate(context, R.layout.in_head_tright, toolbar)
            val txt = findViewById<TextView>(R.id.head_vRight, toolbar)
            txt?.setOnClickListener(this)
            txt?.setText(right)
        }
    }

    protected fun addHeaderRight(@DrawableRes right: Int, @IdRes id: Int) {
        if (toolbar != null && right > 0) {
            val v = View.inflate(context, R.layout.in_head_right, null)
            val img = findViewById<ImageView>(R.id.head_vRight, v)
            img?.id = id
            val array = theme.obtainStyledAttributes(intArrayOf(R.attr.appHeaderHeight))
            val width = array.getDimensionPixelSize(0, 0)
            array.recycle()
            val params = Toolbar.LayoutParams(width, width)
            //params.setMargins(0, 0, width * (rightCount - 1), 0);
            params.gravity = Gravity.RIGHT
            img?.layoutParams = params
            toolbar!!.addView(v)
            img?.setOnClickListener(this)
            img?.setImageResource(right)
        }
    }


    /**
     * 头部
     */
    protected val header: View
        get() = toolbar!!

    val headerHeight: Int
        get() {
            if (toolbar != null) {
                return toolbar!!.height
            }
            return 0
        }

    protected val headerTitle: View
        get() = findViewById(R.id.head_vTitle, toolbar)!!

    protected val headerLeft: View
        get() = findViewById(R.id.head_vLeft, toolbar)!!

    protected val headerRight: View
        get() = findViewById(R.id.head_vRight, toolbar)!!

    val mainView: View
        get() = flyMain!!

    protected fun setLastAct(cls: Class<*>) {
        this.lastAct = cls
    }

    /**
     * 启动Activity
     */
    fun startAct(cls: Class<*>, bundle: Bundle? = null, intent: Intent? = null) {
        var i = intent
        if (i == null)
            i = Intent()
        if (bundle != null)
            i.putExtra(BUNDLE, bundle)
        i.putExtra(LAST_ACT, this.javaClass.simpleName)
        i.setClass(this, cls)
        startActivity(i)
    }


    /**
     * 启动Activity，清空栈 并添加到栈顶，慎用
     */
    protected fun startActClear(cls: Class<*>, bundle: Bundle? = null, intent: Intent? = null) {
        if (app != null) app!!.clear()
        startAct(cls, bundle, intent)
    }

    fun startActForResult(cls: Class<*>, requestCode: Int, bundle: Bundle? = null) {
        val i = Intent(this, cls)
        i.putExtra(LAST_ACT, this.javaClass.simpleName)
        if (bundle != null)
            i.putExtra(BUNDLE, bundle)
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
        if (app != null) app!!.remove(this)
        if (lastAct != null && TextUtils.equals(lastAct!!.simpleName, lastSkipAct)) {
            startActClear(lastAct!!)
        } else
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

    override fun onDestroy() {
        client?.onDestroy()
        super.onDestroy()
    }

    companion object {
        val LAST_ACT = "LAST_ACT"
        val BUNDLE = "TAG_BUNDLE"
    }
}
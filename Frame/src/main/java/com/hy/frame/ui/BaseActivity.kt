package com.hy.frame.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.hy.frame.R
import com.hy.frame.app.IBaseApplication
import com.hy.frame.util.MyLog
import com.hy.frame.util.StatusBarUtil

/**
 * 父类Activity
 * author HeYan
 * time 2015/12/23 16:40
 */
abstract class BaseActivity : AppCompatActivity(), IBaseActivity, IBaseTemplateUI {

    private var mApp: IBaseApplication? = null
    private var mContext: Context? = null
    private var lastSkipAct: String? = null //获取上一级的Activity名

    private var mToolbar: Toolbar? = null
    private var mFlyMain: FrameLayout? = null
    private var mTemplateControl: ITemplateControl? = null

    private var mLastTime: Long = 0

    override fun getCurContext(): Context = mContext!!

    override fun getCurApp(): IBaseApplication = mApp!!

    //未指定
    override fun getScreenOrientation(): Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    //override fun getLayoutId(): Int = 0

    override fun getLayoutView(): View? = null

    override fun getCurActivity(): AppCompatActivity = this

    override fun getTemplateControl(): ITemplateControl? = mTemplateControl

    //override fun getTemplateView(): IBaseTemplateUI? = this

    override fun isFastClick(): Boolean {
        val curTime = System.currentTimeMillis()
        if (curTime - this.mLastTime < 500)
            return true
        this.mLastTime = curTime
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!init())
            return
        if (isPermissionDenied()) {
            finish()
            return
        }
        initLayout()
        initView()
        initData()
    }

    /**
     * 初始化Application
     */
    private fun init(): Boolean {
        lastSkipAct = intent.getStringExtra(LAST_ACT)// 获取上一级Activity的Name
        if (application !is IBaseApplication) {
            MyLog.e("BaseApplication Exception！")
            setContentView(R.layout.v_frame_warn)
            Handler().postDelayed({
                System.exit(0)
            }, 3000L)
            return false
        }
        mApp = application as IBaseApplication
        mApp?.getActivityCache()?.add(this)
        mContext = this
        return true
    }

    /**
     * 初始化布局
     */
    private fun initLayout() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        if (isSingleLayout()) {
            if (getLayoutView() != null) {
                setContentView(getLayoutView())
            } else if (getLayoutId() != 0) {
                setContentView(getLayoutId())
            }
        } else {
            setContentView(R.layout.v_base)
        }
        mToolbar = findViewById(R.id.head_toolBar)
        mFlyMain = findViewById(R.id.base_flyMain)
        if (!isSingleLayout() && mFlyMain != null) {
            if (getLayoutView() != null) {
                mFlyMain?.addView(getLayoutView())
            } else if (getLayoutId() != 0) {
                View.inflate(getCurContext(), getLayoutId(), mFlyMain)
            }
        }
        initToolbar()
        this.mTemplateControl = TemplateControl()
        this.mTemplateControl?.init(this, this.mToolbar, this.mFlyMain)
    }


    private fun initToolbar() {
        if (mToolbar == null) return
        mToolbar!!.setTitle(R.string.empty)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isTranslucentStatus()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                val statusBarHeight = getStatusBarHeight()
                if (statusBarHeight > 0) {
                    mToolbar!!.setPadding(0, statusBarHeight, 0, 0)
                    if (mToolbar!!.layoutParams != null) {
                        val params = mToolbar!!.layoutParams
                        params.height = resources.getDimensionPixelSize(R.dimen.header_height) + statusBarHeight
                        mToolbar!!.layoutParams = params
                    }
                }
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }
        if (mToolbar != null)
            setSupportActionBar(mToolbar)
    }

    override fun getStatusBarHeight(): Int {
        return StatusBarUtil.getStatusBarHeight(mContext!!)
    }

    /**
     * 设置标题
     */
    override fun setTitle(@StringRes titleId: Int) {
        this.mTemplateControl?.setTitle(getString(titleId))
    }

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
            return intent.extras
        }

    override fun finish() {
        this.mTemplateControl?.onDestroy()
        this.mTemplateControl = null
        this.mApp?.getActivityCache()?.remove(this)
        this.mApp = null
        this.mToolbar = null
        this.mFlyMain = null
        this.mContext = null
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
        if (isFastClick())
            return
        onViewClick(v)
    }

    override fun onLeftClick() {
        onBackPressed()
    }

    override fun onRightClick() {}

    override fun onLoadViewClick() {}

    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    override fun <T : View> findViewById(@IdRes id: Int, parent: View?): T? {
        return parent?.findViewById(id) ?: findViewById(id)
    }

    /**
     * 获取并绑定点击
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    override fun <T : View> setOnClickListener(@IdRes id: Int, parent: View?): T? {
        val view = findViewById<T>(id, parent)
        view?.setOnClickListener(this)
        return view
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        const val LAST_ACT = "LAST_ACT"
        const val BUNDLE = "TAG_BUNDLE"
    }
}
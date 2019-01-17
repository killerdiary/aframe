package com.hy.frame.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.support.v7.widget.Toolbar
import com.hy.frame.R
import com.hy.frame.app.IBaseApplication
import com.hy.frame.util.HyUtil

/**
 * 父类Fragment
 * author HeYan
 * time 2015/12/23 17:12
 */
abstract class BaseFragment : Fragment(), IBaseFragment {

    private var mContentView: View? = null

    private var mToolbar: Toolbar? = null
    private var mFlyMain: FrameLayout? = null

    private var mTemplateControl: ITemplateControl? = null

    override fun getTemplateView(): IBaseTemplateView? = this

    private var mLastTime: Long = 0


    private var mShowCount: Int = 0
    private var mInit: Boolean = false
    override fun isInit(): Boolean = mInit
    override fun isTranslucentStatus(): Boolean {
        if (activity != null && activity is IBaseTemplateView) {
            val template = activity as IBaseTemplateView
            return template.isTranslucentStatus()
        }
        return false
    }


    override fun getCurContext(): Context = context!!

    override fun getCurApp(): IBaseApplication {
        return (activity!! as IBaseTemplateView).getCurApp()
    }

    override fun getCurActivity(): AppCompatActivity {
        return activity!! as AppCompatActivity
    }

    override fun getStatusBarHeight(): Int {
        if (activity != null && activity is IBaseTemplateView) {
            val template = activity as IBaseTemplateView
            return template.getStatusBarHeight()
        }
        return 0
    }

    override fun getLayoutView(): View? = null

    override fun getFragment(): Fragment = this

    override fun getTemplateControl(): ITemplateControl? = mTemplateControl

    override fun isFastClick(): Boolean {
        val curTime = System.currentTimeMillis()
        if (curTime - this.mLastTime < 500)
            return true
        this.mLastTime = curTime
        return false
    }

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

    override fun onDestroy() {
        this.mTemplateControl = null
        this.mToolbar = null
        this.mFlyMain = null
        this.mInit = false
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // MyLog.d(getClass(), "onCreateView");
        mShowCount++
        if (mContentView == null) {
            var v: View? = null
            if (isSingleLayout()) {
                if (getLayoutView() != null) {
                    v = getLayoutView()
                } else if (getLayoutId() != 0) {
                    v = inflater.inflate(getLayoutId(), container, false)
                }
            } else {
                v = inflater.inflate(R.layout.v_base, container, false)
            }
            mFlyMain = findViewById(R.id.base_flyMain, v)
            mToolbar = findViewById(R.id.head_toolBar, v)
            if (!isSingleLayout() && mFlyMain != null) {
                if (getLayoutView() != null) {
                    mFlyMain?.addView(getLayoutView())
                } else if (getLayoutId() != 0) {
                    View.inflate(getCurContext(), getLayoutId(), mFlyMain)
                }
            }
            mContentView = v
            mInit = false
            initToolbar()
            this.mTemplateControl = TemplateControl()
            this.mTemplateControl?.init(this, this.mToolbar, this.mFlyMain)
            initView()
        }
        return mContentView
    }

    private fun initToolbar() {
        if (mToolbar == null) return
        mToolbar!!.setTitle(R.string.empty)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val statusBarHeight = getStatusBarHeight()
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
     * 启动Activity
     */
    override fun startAct(cls: Class<*>, bundle: Bundle?, intent: Intent?) {
        var i = intent
        if (i == null)
            i = Intent()
        if (bundle != null)
            i.putExtra(BaseActivity.BUNDLE, bundle)
        i.putExtra(BaseActivity.LAST_ACT, this.javaClass.simpleName)
        i.setClass(getCurContext(), cls)
        startActivity(i)
    }

    override fun startActForResult(cls: Class<*>, requestCode: Int, bundle: Bundle?, intent: Intent?) {
        var i = intent
        if (i == null)
            i = Intent()
        if (bundle != null)
            i.putExtra(BaseActivity.BUNDLE, bundle)
        i.putExtra(BaseActivity.LAST_ACT, this.javaClass.simpleName)
        i.setClass(getCurContext(), cls)
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
        onViewClick(v)
    }

    override fun onLeftClick() {

    }

    override fun onRightClick() {

    }

    override fun onLoadViewClick() {

    }

    override fun <T : View> findViewById(id: Int): T? {
        return findViewById(id, null)
    }

    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    override fun <T : View> findViewById(@IdRes id: Int, parent: View?): T? {
        return parent?.findViewById(id) ?: mContentView?.findViewById(id)
    }

    /**
     * 获取并绑定点击
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    override fun <T : View> setOnClickListener(@IdRes id: Int, parent: View?): T? {
        val view = findViewById<T>(id, parent)
        view?.setOnClickListener(this) ?: return null
        return view
    }


}

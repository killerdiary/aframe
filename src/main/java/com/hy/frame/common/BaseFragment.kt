package com.hy.frame.common

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.hy.frame.R
import com.hy.frame.bean.LoadCache
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.http.MyHttpClient

/**
 * 父类Fragment
 * author HeYan
 * time 2015/12/23 17:12
 */
abstract class BaseFragment : Fragment(), android.view.View.OnClickListener, IFragmentListener, IBaseActivity {
    private var contentView: View? = null
    // private boolean custom;
    var app: BaseApplication? = null
    //protected var context: Context? = null
    private var toolbar: Toolbar? = null
    //private TextView txtTitle;
    private var flyMain: FrameLayout? = null
    protected var loadCache: LoadCache? = null
    var showCount: Int = 0
    var isInit: Boolean = false
    protected open var client: MyHttpClient? = null

    protected val isTranslucentStatus: Boolean
        get() {
            if (activity != null && activity is BaseActivity) {
                val act = activity as BaseActivity
                return act.isTranslucentStatus()
            }
            return false
        }

    /**
     * 唯一布局ID

     * @return
     */
    protected fun initSingleLayoutId(): Int {
        return 0
    }

    override fun onStart() {
        super.onStart()
        if (!isInit) {
            isInit = true
            initData()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // MyLog.d(getClass(), "onCreateView");
        showCount++
        if (contentView == null) {
            //context = activity
            app = activity.application as BaseApplication
            val v: View?
            if (initSingleLayoutId() != 0) {
                val v1 = inflater!!.inflate(initSingleLayoutId(), container, false)
                flyMain = getView<FrameLayout>(R.id.base_flyMain, v1)
                if (flyMain == null) {
                    v = inflater.inflate(R.layout.act_base_fragment, container, false)
                    flyMain = getView<FrameLayout>(R.id.base_flyMain, v)
                    View.inflate(context, initSingleLayoutId(), flyMain)
                } else {
                    v = v1
                }
            } else if (initLayoutId() != 0) {
                v = inflater!!.inflate(R.layout.act_base, container, false)
                flyMain = getView<FrameLayout>(R.id.base_flyMain, v)
                View.inflate(context, initLayoutId(), flyMain)
            } else {
                MyLog.e(javaClass, "initLayoutId not call")
                return null
            }
            toolbar = getView<Toolbar>(R.id.head_toolBar, v)
            contentView = v
            isInit = false
            initToolbar()
            initView()
        }
        return contentView
    }

    private fun initToolbar() {
        if (toolbar == null) return
        toolbar!!.title = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //toolbar.setBackgroundResource(R.color.blue);
            val statusBarHeight = (activity as BaseActivity).statusBarHeight
            if (isTranslucentStatus && statusBarHeight > 0) {
                toolbar!!.setPadding(0, statusBarHeight, 0, 0)
                if (toolbar!!.layoutParams != null)
                    toolbar!!.layoutParams.height = resources.getDimensionPixelSize(R.dimen.header_height) + statusBarHeight
            }
        }
    }

    /**
     * 加载布局
     */
    protected fun initLoadView(): Boolean {
        if (flyMain == null) {
            MyLog.e(javaClass, "Your layout must include 'FrameLayout',the ID must be 'base_flyMain'!")
            return false
        }
        if (loadCache != null) return true
        var loadView: View? = getView(R.id.base_llyLoad)
        //You need to add the layout
        if (loadView == null) {
            if (flyMain!!.childCount > 0) {
                loadView = View.inflate(context, R.layout.in_loading, null)
                flyMain!!.addView(loadView, 0)
            } else
                View.inflate(context, R.layout.in_loading, flyMain)
        }
        loadCache = LoadCache()
        loadCache!!.llyLoad = getView <LinearLayout>(R.id.base_llyLoad)
        loadCache!!.proLoading = getView<ProgressBar>(R.id.base_proLoading)
        loadCache!!.imgMessage = getView<ImageView>(R.id.base_imgMessage)
        loadCache!!.txtMessage = getView<TextView>(R.id.base_txtMessage)
        return true
    }

    protected fun showLoading(msg: String = getString(R.string.loading)) {
        if (initLoadView()) {
            val count = flyMain!!.childCount
            for (i in 0..count - 1) {
                val v = flyMain!!.getChildAt(i)
                if (i > 0) v.visibility = View.GONE
            }
            loadCache!!.showLoading(msg)
        }
    }

    //R.drawable.img_hint_net_fail
    protected fun showNoData(msg: String = getString(R.string.hint_nodata), drawId: Int = R.mipmap.img_hint_nodata) {
        if (initLoadView()) {
            val count = flyMain!!.childCount
            for (i in 0..count - 1) {
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

    protected fun onRetryRequest() {}

    /**
     * 显示内容View
     */
    protected fun showCView() {
        if (initLoadView()) {
            val count = flyMain!!.childCount
            for (i in 0..count - 1) {
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
    protected fun setTitle(@StringRes titleId: Int) {
        setTitle(getString(titleId))
    }

    /**
     * 设置标题
     */
    protected fun setTitle(title: CharSequence) {
        if (toolbar!!.findViewById(R.id.head_vTitle) == null) {
            val v = View.inflate(context, R.layout.in_head_title, null)
            val tlp = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT)
            tlp.gravity = Gravity.CENTER
            toolbar!!.addView(v, tlp)
        }
        val txtTitle = getView<TextView>(R.id.head_vTitle, toolbar)
        txtTitle.text = title
    }

    protected fun hideHeader() {
        if (toolbar != null) toolbar!!.visibility = View.GONE
    }

    protected fun setHeaderLeft(@DrawableRes left: Int) {
        if (left > 0) {
            if (toolbar!!.findViewById(R.id.head_vLeft) == null) {
                val v = View.inflate(context, R.layout.in_head_left, toolbar)
                val img = getView<ImageView>(R.id.head_vLeft, v)
                img.setOnClickListener(this)
                img.setImageResource(left)
            } else {
                val img = getView<ImageView>(R.id.head_vLeft, toolbar)
                img.setImageResource(left)
            }
        }
    }

    protected fun setHeaderLeftTxt(@StringRes left: Int) {
        if (left > 0) {
            if (toolbar!!.findViewById(R.id.head_vLeft) == null) {
                val v = View.inflate(context, R.layout.in_head_tleft, toolbar)
                val txt = getView<TextView>(R.id.head_vLeft, v)
                txt.setOnClickListener(this)
                txt.setText(left)
            } else {
                val txt = getView<TextView>(R.id.head_vLeft, toolbar)
                txt.setText(left)
            }
        }
    }

    protected fun setHeaderRight(@DrawableRes right: Int) {
        if (right > 0) {
            if (toolbar!!.findViewById(R.id.head_vRight) == null) {
                val v = View.inflate(context, R.layout.in_head_right, toolbar)
                val img = getView<ImageView>(R.id.head_vRight, v)
                img.setOnClickListener(this)
                img.setImageResource(right)
            } else {
                val img = getView<ImageView>(R.id.head_vRight, toolbar)
                img.setImageResource(right)
            }
        }
    }

    protected fun addHeaderRight(@DrawableRes right: Int, @IdRes id: Int) {
        val v = View.inflate(context, R.layout.in_head_right, null)
        val img = getView<ImageView>(R.id.head_vRight, v)
        img.id = id
        val array = activity.theme.obtainStyledAttributes(intArrayOf(R.attr.appHeaderHeight))
        val width = array.getDimensionPixelSize(0, 0)
        array.recycle()
        val params = Toolbar.LayoutParams(width, width)
        //params.setMargins(0, 0, width * (rightCount - 1), 0);
        params.gravity = Gravity.RIGHT
        img.layoutParams = params
        toolbar!!.addView(v)
        img.setOnClickListener(this)
        img.setImageResource(right)
    }

    protected fun setHeaderRightTxt(@StringRes right: Int) {
        if (right > 0) {
            if (toolbar!!.findViewById(R.id.head_vRight) == null) {
                val v = View.inflate(context, R.layout.in_head_tright, toolbar)
                val txt = getView<TextView>(R.id.head_vRight, v)
                txt.setOnClickListener(this)
                txt.setText(right)
            } else {
                val txt = getView<TextView>(R.id.head_vRight, toolbar)
                txt.setText(right)
            }
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
        get() = toolbar!!.findViewById(R.id.head_vTitle)

    protected val headerLeft: View
        get() = toolbar!!.findViewById(R.id.head_vLeft)

    protected val headerRight: View
        get() = toolbar!!.findViewById(R.id.head_vRight)

    protected val mainView: View
        get() = flyMain!!

    /**
     * 启动Activity
     */
    protected fun startAct(cls: Class<*>, bundle: Bundle? = null, intent: Intent? = null) {
        var i = intent
        if (i == null)
            i = Intent()
        if (bundle != null)
            i.putExtra(BaseActivity.BUNDLE, bundle)
        i.putExtra(BaseActivity.LAST_ACT, this.javaClass.simpleName)
        i.setClass(activity, cls)
        startActivity(intent)
    }

    fun startActForResult(cls: Class<*>, requestCode: Int, bundle: Bundle? = null) {
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
        if (v.id == R.id.head_vLeft)
            onLeftClick()
        else if (v.id == R.id.head_vRight)
            onRightClick()
        else if (v.id == R.id.base_llyLoad)
            onRetryRequest()
        else
            onViewClick(v)
    }


    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param v  Layout
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> getView(@IdRes id: Int, v: View? = null): T {
        if (v == null)
            return contentView!!.findViewById(id) as T
        return v.findViewById(id) as T
    }

    /**
     * 获取并绑定点击
     * @param id 行布局中某个组件的id
     * @param v  Layout
     */
    @Suppress("UNCHECKED_CAST")
    protected fun <T : View> getViewAndClick(@IdRes id: Int, v: View? = null): T {
        val view = getView<View>(id, v)
        view.setOnClickListener(this)
        return view as T
    }

    protected fun setOnClickListener(@IdRes id: Int, v: View? = null) {
        val view = getView<View>(id, v)
        view.setOnClickListener(this)
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
        if (client != null) {
            client!!.onDestroy()
        }
        super.onDestroy()
    }
}
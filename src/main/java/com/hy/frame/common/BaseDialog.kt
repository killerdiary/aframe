package com.hy.frame.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.View
import android.view.WindowManager.LayoutParams
import com.hy.frame.R
import com.hy.frame.util.HyUtil

/**
 * @author HeYan
 * @title 父类对话框
 * @time 2015/11/16 13:25
 */
abstract class BaseDialog(context: Context) : Dialog(context, R.style.AppBaseTheme_DialogTheme), View.OnClickListener {
    var listener: IConfirmListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initLayoutId())
        initWindow()
        initView()
        initData()
    }

    protected abstract fun initLayoutId(): Int

    /**
     * 初始化Window
     */
    protected abstract fun initWindow()

    /**
     * 初始化布局
     */
    protected abstract fun initView()

    /**
     * 初始化数据
     */
    protected abstract fun initData()

    /**
     * 控件点击事件
     */
    protected abstract fun onViewClick(v: View)

    override fun onClick(v: View) {
        if (HyUtil.isFastClick)
            return
        onViewClick(v)
    }

    protected fun windowDeploy(width: Float, height: Float, gravity: Int) {
        val window = window ?: return
        val params = getWindow()!!.attributes // 获取对话框当前的参数值
        if (width == 0f) {
            params.width = LayoutParams.WRAP_CONTENT
        } else if (width > 0 && width <= 1) {
            params.width = (context.resources.displayMetrics.widthPixels * width).toInt() // 宽度设置为屏幕的0.x
        } else {
            params.width = width.toInt()
        }
        if (height == 0f) {
            params.height = LayoutParams.WRAP_CONTENT
        } else if (height > 0 && height <= 1) {
            params.height = (context.resources.displayMetrics.heightPixels * height).toInt() // 高度设置为屏幕的0.x
        } else {
            params.height = height.toInt()
        }
        params.verticalMargin = -0.1f
        window.attributes = params // 设置生效
        getWindow()!!.setGravity(gravity)
        // getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
        setCanceledOnTouchOutside(false)// 设置触摸对话框意外的地方取消对话框
        setCancelable(false)
        // window.setWindowAnimations(R.style.winAnimFadeInFadeOut);
    }

    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> findViewById(@IdRes id: Int, parent: View?): T? {
        val view = parent?.findViewById<View>(id) ?: findViewById<View>(id)
        return if (view == null) null else view as T
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

    var tag: Any? = null

    interface IConfirmListener {
        fun onDlgConfirm(dialog: BaseDialog)
    }
}

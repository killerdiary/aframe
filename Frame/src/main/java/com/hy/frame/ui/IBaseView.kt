package com.hy.frame.ui

import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.view.View

/**
 * 基础的视图interface，作用范围[Activity,Fragment,Dialog]
 * @author
 * @time 18-10-22 上午10:21
 */
interface IBaseView : android.view.View.OnClickListener{

    fun getCurContext(): Context

    /**
     * LayoutId 默认值为0
     */
    @LayoutRes
    fun getLayoutId(): Int

    /**
     * Layout View 不为空时优先使用
     */
    fun getLayoutView(): View?

    /**
     * 初始化布局
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 控件点击事件
     */
    fun onViewClick(v: android.view.View)

    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    fun <T : View> findViewById(@IdRes id: Int, parent: View?): T?

    /**
     * 获取并绑定点击
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    fun <T : View> setOnClickListener(@IdRes id: Int, parent: View? = null): T?

    /**
     * 是否是快速点击
     */
    fun isFastClick(): Boolean
}
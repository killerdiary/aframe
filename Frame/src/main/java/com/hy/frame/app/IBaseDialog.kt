package com.hy.frame.app

/**
 * Dialog Interface
 *
 * @author HeYan
 * @time 2018/4/6 16:03
 */
interface IBaseDialog {
    /**
     * LayoutId
     */
    fun getLayoutId(): Int

    /**
     * 初始化Window
     */
    fun initWindow()

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
}
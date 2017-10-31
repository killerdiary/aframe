package com.hy.frame.common

import android.support.annotation.LayoutRes
import android.view.View

/**
 * @author HeYan
 * @title 公有接口
 * @time 2015/11/20 11:30
 */
interface IBaseActivity {
    /**
     * 初始化布局 可空,可以包含 Toolbar 否则 使用默认的 Toolbar
     */
    @LayoutRes
    fun initLayoutId(): Int

    /**
     * 初始化控件
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initData()
    
    /**
     * 请求数据
     */
    fun requestData()

    /**
     * 更新UI
     */
    fun updateUI()

    /**
     * 控件点击事件
     */
    fun onViewClick(v: View)

    /**
     * 头-左边图标点击
     */
    fun onLeftClick()

    /**
     * 头-右边图标点击
     */
    fun onRightClick()
}

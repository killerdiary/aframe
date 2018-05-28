package com.hy.frame.app

import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
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
    fun getLayoutId(): Int

    /**
     * 唯一布局ID
     */
    fun isSingleLayout(): Boolean

    /**
     * 是否竖屏
     */
    fun isPortrait(): Boolean

    /**
     * 判断是否拥有进入权限
     */
    fun isPermissionDenied(): Boolean

    /**
     * 是否开启透明状态栏
     */
    fun isTranslucentStatus(): Boolean

    /**
     * 状态栏高度
     */
    fun getStatusBarHeight(): Int

    /**
     * 初始化控件
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initData()

//    /**
//     * 请求数据
//     */
//    fun requestData()

    //    /**
//     * 更新UI
//     */
//    fun updateUI()
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

    fun hideHeader()

    fun setHeaderLeft(@DrawableRes left: Int)

    fun setHeaderLeftTxt(@StringRes left: Int)

    fun setHeaderRight(@DrawableRes right: Int)

    fun setHeaderRightTxt(@StringRes right: Int)

    fun addHeaderRight(@DrawableRes right: Int, @IdRes id: Int)

    fun addHeaderRightPath(@DrawableRes rightPath: String?, @IdRes id: Int)
}

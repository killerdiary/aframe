package com.hy.frame.ui

/**
 * @author HeYan
 * @title 公有接口
 * @time 2015/11/20 11:30
 */
interface IBaseActivity {

    /**
     * 屏幕方向，可以用Activity.setRequestedOrientation替代
     */
    fun getScreenOrientation(): Int

    /**
     * 判断是否拥有进入权限
     */
    fun isPermissionDenied(): Boolean

}

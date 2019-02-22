package com.hy.frame.ui

import android.support.annotation.IdRes
import android.view.View

/**
 * @author HeYan
 * @title 公有接口
 * @time 2015/11/20 11:30
 */
interface IBaseFragment {

    /**
     * 重启
     */
    fun onRestart()

    /**
     * 是否已经初始化布局
     */
    fun isInit(): Boolean
    /**
     * 方便传递消息
     */
    fun sendMsg(flag: Int, obj: Any?)

    fun <T : View> findViewById(@IdRes id: Int): T?

    fun getFragment(): Fragment
}

package com.hy.frame.mvp

import android.content.Context

/**
 * MVP中Presenter需要实现的Interface
 *
 * @author HeYan
 * @time 2018/4/4 9:41
 */
interface IBasePresenter {
    fun getContext(): Context
    /**
     * 释放资源
     */
    fun onDestroy()
}
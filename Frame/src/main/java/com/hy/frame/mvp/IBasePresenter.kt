package com.hy.frame.mvp

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context

/**
 * MVP中Presenter需要实现的Interface
 *
 * @author HeYan
 * @time 2018/4/4 9:41
 */
interface IBasePresenter : GenericLifecycleObserver {
    fun getContext(): Context
    /**
     * 释放资源
     */
    fun onDestroy()

    override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
        if (event != null && event == Lifecycle.Event.ON_DESTROY) {
            onDestroy()
        }
    }
}
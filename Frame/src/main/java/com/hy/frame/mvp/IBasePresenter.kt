package com.hy.frame.mvp

import android.content.Context
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

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
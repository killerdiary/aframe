package com.hy.frame.bean

import android.app.Activity
import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

/**
 * 避免内存泄漏Handler
 * @author HeYan
 * @time 2017/9/14 9:53
 */
class MyHandler(act: Activity, private val listener: HandlerListener) : Handler(act.mainLooper) {
    private val mActivity: WeakReference<Activity> = WeakReference(act)

    override fun handleMessage(msg: Message) {
        val activity = mActivity.get()
        if (activity != null) {
            listener.handleMessage(msg)
        }
    }

    interface HandlerListener {
        fun handleMessage(msg: Message)
    }
}
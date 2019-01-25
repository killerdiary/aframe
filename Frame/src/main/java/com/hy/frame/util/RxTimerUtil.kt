package com.hy.frame.util

import android.os.Handler
import android.os.Looper

/**
 * title 定时器_主线程
 * author heyan
 * time 19-1-25 下午5:35
 * desc 无
 */
class RxTimerUtil {

    private var handler: android.os.Handler? = null
    private var isStop = false

    /**
     * milliseconds毫秒后执行next操作
     * @param milliseconds 毫秒
     * @param callback 回调
     */
    fun timer(milliseconds: Long, callback: ICallback?) {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        isStop = false
        handler?.postDelayed({
            if (isStop)
                return@postDelayed
            callback?.doNext()
        }, milliseconds)
    }


    /**
     * 每隔milliseconds毫秒后执行
     *
     * @param milliseconds
     * @param callback 回调
     */
    fun interval(milliseconds: Long, callback: ICallback?) {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        isStop = false
        timerDelayed(milliseconds, callback)
    }

    private fun timerDelayed(milliseconds: Long, callback: ICallback?) {
        handler?.postDelayed({
            if (isStop)
                return@postDelayed
            else {
                timerDelayed(milliseconds, callback)
                callback?.doNext()
            }

        }, milliseconds)
    }

    /**
     * 取消订阅
     */
    fun cancel() {
        isStop = true
    }

    interface ICallback {
        fun doNext()
    }
}
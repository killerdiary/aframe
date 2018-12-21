package com.hy.frame.util

import android.util.Log

/**
 * 日志
 * @author HeYan
 * @time 2014年12月17日 下午4:05:40
 */
object MyLog {

    const val TAG: String = "HyLog"
    var isLoggable = false

    fun i(msg: Any) {
        i(null, msg)
    }

    fun i(cls: Class<*>, msg: Any) {
        i(cls.simpleName, msg)
    }

    fun i(tag: String?, msg: Any) {
        println(Log.INFO, tag, msg)
    }

    fun d(msg: Any) {
        d(null, msg)
    }

    fun d(cls: Class<*>, msg: Any) {
        d(cls.simpleName, msg)
    }

    fun d(tag: String?, msg: Any) {
        println(Log.DEBUG, tag, msg)
    }

    fun w(msg: Any) {
        w(null, msg)
    }

    fun w(cls: Class<*>, msg: Any) {
        w(cls.simpleName, msg)
    }

    fun w(tag: String?, msg: Any) {
        println(Log.WARN, tag, msg)
    }

    fun e(msg: Any) {
        e(null, msg)
    }

    fun e(cls: Class<*>, msg: Any) {
        e(cls.simpleName, msg)
    }

    fun e(tag: String?, msg: Any) {
        println(Log.ERROR, tag, msg)
    }

    private fun println(priority: Int, tag: String?, msg: Any) {
        if (isLoggable) {
            Log.println(priority, TAG, if (tag.isNullOrEmpty()) msg.toString() else tag + ": " + msg.toString())
        }
    }
}

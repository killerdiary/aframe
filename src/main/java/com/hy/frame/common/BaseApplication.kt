package com.hy.frame.common

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.support.annotation.CallSuper
import android.text.TextUtils
import com.hy.frame.util.MyLog
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 应用
 * @author HeYan
 * @time 2014年12月17日 下午4:19:29
 */
abstract class BaseApplication : Application() {
    /**
     * Activity栈
     */
    private var acts: MutableList<Activity>? = null
    /**
     * 全局数据
     */
    private var hashMap: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()
        MyLog.isLoggable = isLoggable()
        val processName = getProcessName(this, android.os.Process.myPid())
        if (processName != null) {
            MyLog.d(javaClass, "Application start! process:" + processName)
            val defaultProcess = TextUtils.equals(processName, packageName)
            if (defaultProcess) {
                initAppForMainProcess()
            } else {
                initAppForOtherProcess(processName)
            }
        }
    }

    override fun onTerminate() {
        // 程序终止的时候执行
        MyLog.d(javaClass, "Application closed! onTerminate")
        super.onTerminate()
    }

    override fun onLowMemory() {
        // 低内存的时候执行
        MyLog.d(javaClass, "Application onLowMemory")
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        // 程序在内存清理的时候执行
        MyLog.d(javaClass, "Application onTrimMemory level=$level")
        super.onTrimMemory(level)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        MyLog.d(javaClass, "Application onConfigurationChanged newConfig=" + newConfig)
        super.onConfigurationChanged(newConfig)
    }


    /**
     * 添加Activity到容器中
     */
    fun addActivity(activity: Activity) {
        //防止重复添加
        remove(activity)
        if (acts == null)
            acts = CopyOnWriteArrayList<Activity>()
        acts?.add(activity)
    }

    /**
     * 清理activity栈 并退出
     */
    fun exit() {
        clear()
        System.exit(0)
    }

    /**
     * 清理activity栈
     */
    fun remove(activity: Activity) {
        acts?.remove(activity)
    }

    /**
     * 清理activity栈
     */
    fun clear(cls: Class<*>? = null) {
        if (acts != null && acts!!.isNotEmpty()) {
            if (cls != null) {
                val activity: Activity? = acts!!.firstOrNull { TextUtils.equals(it.javaClass.name, cls.name) }
                if (activity != null) {
                    activity.finish()
                    acts!!.remove(activity)
                }
            } else {
                for (activity in acts!!) {
                    activity.finish()
                }
                acts!!.clear()
            }
        }
    }

    /**
     * 存数据

     * @param key
     *
     * @param value
     */
    fun putValue(key: String, value: Any?) {
        if (hashMap == null) {
            hashMap = HashMap()
        }
        if (value == null)
            hashMap!!.remove(key)
        else
            hashMap!!.put(key, value)
    }

    /**
     * 取数据
     */
    fun getValue(key: String): Any? {
        if (hashMap != null) {
            return hashMap!![key]
        }
        return null
    }

    /**
     * 主线程方法
     */
    @CallSuper
    protected open fun initAppForMainProcess() {

    }

    /**
     * 其它线程方法
     */
    protected open fun initAppForOtherProcess(process: String) {

    }

    private fun getProcessName(cxt: Context, pid: Int): String? {
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        return runningApps
                .firstOrNull { it.pid == pid }
                ?.processName
    }

    abstract fun isLoggable(): Boolean
}

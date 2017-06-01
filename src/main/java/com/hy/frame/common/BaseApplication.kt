package com.hy.frame.common

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.support.annotation.CallSuper
import android.text.TextUtils
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 应用

 * @author HeYan
 * *
 * @time 2014年12月17日 下午4:19:29
 */
class BaseApplication : Application() {
    /**
     * Activity栈
     */
    private var acts: MutableList<Activity>? = null
    /**
     * 全局数据
     */
    private var hashMap: HashMap<String, Any>? = null
    private var receiver: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        val processName = HyUtil.getProcessName(this, android.os.Process.myPid())
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
        if (receiver != null) {
            try {
                unregisterReceiver(receiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        super.onTerminate()
    }

    override fun onLowMemory() {
        // 低内存的时候执行
        MyLog.d(javaClass, "onLowMemory")
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        // 程序在内存清理的时候执行
        MyLog.d(javaClass, "onTrimMemory")
        super.onTrimMemory(level)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        MyLog.d(javaClass, "onConfigurationChanged")
        super.onConfigurationChanged(newConfig)
    }


    /**
     * 添加Activity到容器中
     */
    fun addActivity(activity: Activity) {
        //防止重复添加
        remove(activity)
        acts!!.add(activity)
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
        if (acts != null && !acts!!.isEmpty()) {
            acts!!.remove(activity)
        }
    }

    /**
     * 清理activity栈
     */
    fun clear(cls: Class<*>? = null) {
        if (acts != null && !acts!!.isEmpty()) {
            if (cls != null) {
                var activity: Activity? = null
                for (item in acts!!) {
                    if (TextUtils.equals(item.javaClass.name, cls.name)) {
                        activity = item
                        break
                    }
                }
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
     * *
     * @param value
     */
    fun putValue(key: String, value: Any) {
        if (hashMap == null) {
            hashMap = HashMap<String, Any>()
        }
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
    protected fun initAppForMainProcess(filter: IntentFilter? = null) {
        if (filter != null) {
            filter.priority = IntentFilter.SYSTEM_LOW_PRIORITY
            if (receiver == null)
                receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        val action = intent.action
                        MyLog.d(javaClass, "action:" + action)
                        this@BaseApplication.onReceive(this, intent)
                    }
                }
            registerReceiver(receiver, filter)
        }
        acts = CopyOnWriteArrayList<Activity>()
        hashMap = HashMap<String, Any>()
    }

    /**
     * 其它线程方法
     */
    protected fun initAppForOtherProcess(process: String) {

    }

    protected fun onReceive(receiver: BroadcastReceiver, intent: Intent) {

    }
}

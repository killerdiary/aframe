package com.hy.frame.app

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.support.annotation.CallSuper
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import com.hy.frame.util.MyLog
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 应用
 * @author HeYan
 * @time 2014年12月17日 下午4:19:29
 */
abstract class BaseApplication : MultiDexApplication(), IBaseApplication, IBaseApplication.IActivityCache, IBaseApplication.IDataCache {

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
        val processName = getProcessName(this, android.os.Process.myPid())
        if (processName != null) {
            val defaultProcess = TextUtils.equals(processName, packageName)
            if (defaultProcess) {
                initAppForMainProcess()
            } else {
                initAppForOtherProcess(processName)
            }
        }
    }

    override fun getApplication(): Application = this

    override fun getActivityCache(): IBaseApplication.IActivityCache = this

    override fun getDataCache(): IBaseApplication.IDataCache = this

    @CallSuper
    override fun initAppForMainProcess() {
        MyLog.isLoggable = isLoggable()
        MyLog.d(javaClass, "Application start! process:Main")
    }

    @CallSuper
    override fun initAppForOtherProcess(process: String) {
        MyLog.isLoggable = isLoggable()
        MyLog.d(javaClass, "Application start! process:$process")
    }

    /**
     * 清理activity栈 并退出
     */
    override fun exit() {
        getActivityCache().clear()
        System.exit(0)
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
        MyLog.d(javaClass, "Application onConfigurationChanged newConfig=$newConfig")
        super.onConfigurationChanged(newConfig)
    }

    override fun add(activity: Activity) {
        //防止重复添加
        remove(activity)
        if (acts == null)
            acts = CopyOnWriteArrayList<Activity>()
        acts?.add(activity)
    }

    override fun remove(activity: Activity) {
        if (acts != null && acts!!.isNotEmpty()) {
            val act: Activity? = acts!!.firstOrNull { it == activity }
            if (act != null)
                acts!!.remove(act)
        }
    }

    /**
     * 清理activity栈
     */
    override fun clear() {
        if (acts != null && acts!!.isNotEmpty()) {
            for (act in acts!!) {
                act.finish()
            }
            acts!!.clear()
        }
    }

    /**
     * activity栈数量
     */
    override fun actSize(): Int {
        return acts?.size ?: 0
    }

    override fun getAct(index: Int): Activity? {
        if (acts != null && index < acts!!.size && index >= 0) {
            return acts!![index]
        }
        return null
    }

    /**
     * 存数据
     * @param key
     * @param value
     */
    override fun putValue(key: String, value: Any?) {
        if (hashMap == null) {
            hashMap = HashMap()
        }
        if (value == null)
            hashMap!!.remove(key)
        else
            hashMap!![key] = value
    }

    /**
     * 取数据
     */
    override fun getValue(key: String): Any? {
        if (hashMap != null) {
            return hashMap!![key]
        }
        return null
    }


    private fun getProcessName(cxt: Context, pid: Int): String? {
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        return runningApps
                .firstOrNull { it.pid == pid }
                ?.processName
    }

//    override fun attachBaseContext(base: Context?) {
//        super.attachBaseContext(base)
//        MultiDex.install(this)
//    }
}

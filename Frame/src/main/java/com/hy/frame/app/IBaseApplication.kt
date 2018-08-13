package com.hy.frame.app

import android.app.Activity
import android.app.Application

/**
 * Application Interface
 *
 * @author HeYan
 * @time 2018/4/5 11:26
 */
interface IBaseApplication {

    /**
     * Log 开关
     */
    fun isLoggable(): Boolean
    /**
     * 是否开启MultiDex，如需开启需要同时配置gradle defaultConfig {multiDexEnabled true}
     */
    fun isMultiDex(): Boolean

    /**
     * 主进程方法
     */
    fun initAppForMainProcess()

    /**
     * 其它进程方法
     */
    fun initAppForOtherProcess(process: String)

    /**
     * 退出
     */
    fun exit()

    /**
     * 获取当前Application
     */
    fun getApplication(): Application

    /**
     * 获取当前IActivityCache
     */
    fun getActivityCache(): IActivityCache

    /**
     * 获取当前IDataCache
     */
    fun getDataCache(): IDataCache

    /**
     * Activity Cache
     */
    interface IActivityCache {
        /**
         * 添加Activity到容器中
         */
        fun add(activity: Activity)

        /**
         * remove activity栈
         */
        fun remove(activity: Activity)

        /**
         * 清理activity栈
         * finish and remove
         */
        fun clear()

        /**
         * activity栈数量
         */
        fun actSize(): Int

        /**
         * 获取activity
         * @param index 位置
         */
        fun getAct(index: Int): Activity?
    }

    /**
     * Data Cache
     */
    interface IDataCache {
        /**
         * 存数据
         * @param key
         * @param value
         */
        fun putValue(key: String, value: Any?)

        /**
         * 取数据
         */
        fun getValue(key: String): Any?
    }
}
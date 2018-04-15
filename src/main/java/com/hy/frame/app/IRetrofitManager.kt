package com.hy.frame.app

/**
 * RetrofitManager Interface
 *
 * @author HeYan
 * @time 2018/4/6 17:52
 */
interface IRetrofitManager {
    fun <T> obtainRetrofitService(cls: Class<T>): T?

    fun setToken(token: String?)
}
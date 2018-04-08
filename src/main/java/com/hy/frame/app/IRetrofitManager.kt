package com.hy.frame.app

import com.hy.frame.mvp.IBaseService

/**
 * RetrofitManager Interface
 *
 * @author HeYan
 * @time 2018/4/6 17:52
 */
interface IRetrofitManager {
    fun <T> obtainRetrofitService(cls: Class<T>): T?
}
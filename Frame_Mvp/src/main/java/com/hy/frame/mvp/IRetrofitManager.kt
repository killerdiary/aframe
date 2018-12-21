package com.hy.frame.mvp

import retrofit2.Retrofit

/**
 * RetrofitManager Interface
 *
 * @author HeYan
 * @time 2018/4/6 17:52
 */
interface IRetrofitManager {

    /**
     * 构建Service
     * @param cls 指定Service
     * @param retrofit 自定义Retrofit
     */
    fun <T> obtainService(cls: Class<T>, retrofit: Retrofit? = null): T?

    /**
     * 设置响应类型
     */
    fun setContentType(type: Int): IRetrofitManager

    /**
     * 是否开启日志
     */
    fun setLoggable(enable: Boolean): IRetrofitManager

    /**
     * 添加头信息
     */
    fun addHeader(key: String, value: String?): IRetrofitManager

    /**
     * 设置统一url地址
     */
    fun setBaseUrl(baseUrl: String?): IRetrofitManager

    /**
     * destroy
     */
    fun onDestroy()
}
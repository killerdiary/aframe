package com.hy.frame.net

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 自定义的Okttp Request Interceptor
 * @author
 * @time 18-10-25 下午12:00
 */
class HeaderInterceptor : Interceptor {

    private var headerParams: MutableMap<String, String>? = null //头信息

    /**
     * 添加默认头信息 User-Agent|Content-Type|Accept 无效
     * @param key
     * @param value
     */
    fun addHeader(key: String, value: String?) {
        if (headerParams == null)
            headerParams = LinkedHashMap()
        if (value == null) {
            headerParams!!.remove(key)
        } else {
            headerParams!![key] = value
        }
    }


    fun setHeaders(headerParams: MutableMap<String, String>?) {
        this.headerParams = headerParams
    }

    /**
     * 添加自定义的Headers
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
        if (headerParams != null) {
            for ((key, value) in headerParams!!) {
                builder.addHeader(key, value)
            }
        }
        val request = builder.build()
        return chain.proceed(request)
    }

}
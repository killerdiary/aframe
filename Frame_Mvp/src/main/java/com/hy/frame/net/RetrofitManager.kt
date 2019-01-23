package com.hy.frame.net

import com.hy.frame.mvp.IRetrofitManager
import com.hy.frame.util.MyLog
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager : IRetrofitManager {

    private var mContentType = REQUEST_TYPE_JSON
    private var mLoggable: Boolean = false
    private var mBaseUrl: String? = null

    private var headerParams: MutableMap<String, String>? = null //头信息

    override fun setLoggable(enable: Boolean): IRetrofitManager {
        this.mLoggable = enable
        return this
    }

    override fun setContentType(type: Int): IRetrofitManager {
        this.mContentType = type
        return this
    }

    override fun setBaseUrl(baseUrl: String?): IRetrofitManager {
        this.mBaseUrl = baseUrl
        return this
    }

    /**
     * 添加默认头信息 User-Agent|Content-Type|Accept 无效
     * @param key
     * @param value
     */
    override fun addHeader(key: String, value: String?): IRetrofitManager {
        if (headerParams == null)
            headerParams = LinkedHashMap()
        if (value == null) {
            headerParams!!.remove(key)
        } else {
            headerParams!![key] = value
        }
        return this
    }

    private fun getRetrofit(): Retrofit? {
        if (mBaseUrl == null)
            return null
        val oBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        if (headerParams != null && headerParams!!.isNotEmpty()) {
            val interceptor = HeaderInterceptor()
            interceptor.setHeaders(headerParams)
            oBuilder.addInterceptor(interceptor)
        }
        if (mLoggable) {
            val interceptor = okhttp3.logging.HttpLoggingInterceptor(okhttp3.logging.HttpLoggingInterceptor.Logger { message -> MyLog.d(TAG, message) })
            interceptor.level = okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
            oBuilder.addInterceptor(interceptor)
        }
        val rBuilder = Retrofit.Builder().baseUrl(mBaseUrl!!).client(oBuilder.build()).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        if (mContentType == REQUEST_TYPE_JSON || mContentType == REQUEST_TYPE_JSONARRAY) {
            rBuilder.addConverterFactory(GsonConverterFactory.create())
        }
        return rBuilder.build()
    }

    override fun <T> obtainService(cls: Class<T>, retrofit: Retrofit?): T? {
        if (retrofit != null) {
            return retrofit.create(cls)
        }
        return getRetrofit()?.create(cls)
    }

    override fun onDestroy() {
        this.headerParams = null
        this.mBaseUrl = null
    }

    companion object {
        const val TAG: String = "Retrofit"
        const val REQUEST_TYPE_STRING = 0
        const val REQUEST_TYPE_JSON = 1
        const val REQUEST_TYPE_JSONARRAY = 2
        const val REQUEST_TYPE_FILE = 3
    }
}
package com.hy.frame.mvp.model

import com.hy.frame.mvp.BaseModel
import com.hy.frame.mvp.contract.ApiContract
import com.hy.frame.net.RetrofitManager
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody

/**
 * title 无
 * author heyan
 * time 19-1-18 下午12:07
 * desc 无
 */
class ApiModel : BaseModel(RetrofitManager()), ApiContract.IModel {

    /**
     * [com.hy.frame.mvp.model.ApiService.get]
     */
    override fun get(url: String, params: MutableMap<String, String>?): Observable<ResponseBody>? {
        val service: ApiService? = getRetrofitManager()?.setContentType(RetrofitManager.REQUEST_TYPE_JSON)?.obtainService(ApiService::class.java)
        return service?.get(url, params)
    }

    /**
     * [com.hy.frame.mvp.model.ApiService.post]
     */
    override fun post(url: String, params: MutableMap<String, String>): Observable<ResponseBody>? {
        val service: ApiService? = getRetrofitManager()?.setContentType(RetrofitManager.REQUEST_TYPE_JSON)?.obtainService(ApiService::class.java)
        return service?.post(url, params)
    }

    /**
     * [com.hy.frame.mvp.model.ApiService.post]
     */
    override fun post(url: String, params: Any): Observable<ResponseBody>? {
        val service: ApiService? = getRetrofitManager()?.setContentType(RetrofitManager.REQUEST_TYPE_JSON)?.obtainService(ApiService::class.java)
        return service?.post(url, params)
    }

    /**
     * [com.hy.frame.mvp.model.ApiService.upload]
     */
    override fun upload(url: String, params: MutableMap<String, String>?, file: MultipartBody.Part): Observable<ResponseBody>? {
        val service: ApiService? = getRetrofitManager()?.setContentType(RetrofitManager.REQUEST_TYPE_JSON)?.obtainService(ApiService::class.java)
        return service?.upload(url, params, file)
    }

    /**
     * [com.hy.frame.mvp.model.ApiService.upload]
     */
    override fun upload(url: String, params: MutableMap<String, String>?, files: MutableMap<String, MultipartBody.Part>): Observable<ResponseBody>? {
        val service: ApiService? = getRetrofitManager()?.setContentType(RetrofitManager.REQUEST_TYPE_JSON)?.obtainService(ApiService::class.java)
        return service?.upload(url, params, files)
    }

    /**
     * [com.hy.frame.mvp.model.ApiService.download]
     */
    override fun download(url: String, params: MutableMap<String, String>?): Observable<ResponseBody>? {
        val service: ApiService? = getRetrofitManager()?.setContentType(RetrofitManager.REQUEST_TYPE_FILE)?.obtainService(ApiService::class.java)
        return service?.download(url, params)
    }

}
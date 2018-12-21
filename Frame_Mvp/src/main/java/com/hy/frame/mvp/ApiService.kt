package com.hy.frame.mvp

import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * title 通用Service
 * author heyan
 * time 18-12-4 下午1:15
 * desc 无
 */
interface ApiService {
    @GET("{url}")
    fun executeGet(
            @Path("url") url: String,
            @QueryMap maps: Map<String, String>?): Observable<ResponseBody>?

    @POST("{url}")
    fun executePost(
            @Path("url") url: String,
            @QueryMap maps: Map<String, String>?): Observable<ResponseBody>?


    @Multipart
    @POST("{url}")
    fun upLoadFile(
            @Path("url") url: String,
            @Part("image\";filename=\"image.jpg") avatar: RequestBody?): Observable<ResponseBody>?


    @POST("{url}")
    fun uploadFiles(
            @Path("url") url: String,
            @Path("headers") headers: Map<String, String>,
            @PartMap maps: Map<String, RequestBody>?): Observable<ResponseBody>?


    @Streaming
    @GET("{url}")
    fun downloadFile(@Path("url") url: String): Observable<ResponseBody>?
}
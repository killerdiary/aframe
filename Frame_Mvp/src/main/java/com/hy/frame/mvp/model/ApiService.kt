package com.hy.frame.mvp.model

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * title 通用Service
 * author heyan
 * time 19-1-18 上午11:35
 * desc 无
 */
interface ApiService {
    /**
     * 通用get请求
     * @param url API地址
     *  eg: "/api/user/login"
     *      "http://www.xyz.com/api/user/login"
     * @param params 参数 可空
     *  eg: val params = HashMap<String,String>()
     *      params["name"] = "zhangsan"
     *      params["password"] = "123456"
     * @return Observable<T>?
     */
    @GET
    fun <T> get(@Url url: String, @QueryMap params: MutableMap<String, String>?): Observable<T>?

    /**
     * 通用post请求
     * @param url API地址 eg: 同上 [get]
     * @param params 参数 不能为空 需要添加converter-gson，会转换成json数据
     * @return Observable<T>?
     */
    @POST("{url}")
    fun <T> post(@Url url: String, @Body params: Any): Observable<T>?

    /**
     * 通用post请求
     * @param url API地址 eg: 同上 [get]
     * @param params 参数 不能为空 eg: 同上 [get]
     * @return Observable<T>?
     */
    @FormUrlEncoded
    @POST("{url}")
    fun <T> post(@Url url: String, @FieldMap params: MutableMap<String, String>): Observable<T>?


    /**
     * 通用单文件上传
     * @param url API地址 eg: 同上
     * @param params 参数 可空 eg: 同上
     * @param file 文件 不能为空
     *  eg: val imgPath = ""//文件路径
     *      val fileName =  "${System.currentTimeMillis()}.jpg" //自定义文件名
     *      val file = java.io.File(imgPath)
     *      val body = RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), file)
     *      val part = MultipartBody.Part.createFormData("file", fileName, body)
     * @return Observable<T>?
     */
    @Multipart
    @POST
    fun <T> upload(@Url url: String, @PartMap params: MutableMap<String, String>?, @Part file: MultipartBody.Part): Observable<T>?

    /**
     * 通用多文件文件上传 TODO 待测试
     * @param url API地址 eg: 同上
     * @param params 参数 可空 eg: 同上
     * @param files 文件 不能为空
     *  eg: val imgPath = ""//文件路径
     *      val params = HashMap<String, MultipartBody.Part>()
     *      val fileName =  "${System.currentTimeMillis()}.jpg" //自定义文件名
     *      val file = java.io.File(imgPath)
     *      val body = RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), file)
     *      val part1 = MultipartBody.Part.createFormData("file", fileName, body)
     *      params["file1"] = part1
     * @return Observable<T>?
     */
    @Multipart
    @POST
    fun <T> upload(@Url url: String, @PartMap params: MutableMap<String, String>?, @PartMap files: MutableMap<String, MultipartBody.Part>): Observable<T>?

    /**
     * 通用文件下载
     * @param url API地址 eg: 同上
     * @param params 参数 可空 eg: 同上
     * @return Observable<T>?
     */
    @Streaming
    @GET
    fun <T> download(@Url url: String, @QueryMap params: MutableMap<String, String>?): Observable<T>?
}
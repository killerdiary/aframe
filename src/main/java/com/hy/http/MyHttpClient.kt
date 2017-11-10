package com.hy.http

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.MainThread
import android.support.annotation.StringRes
import android.support.annotation.UiThread
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hy.frame.R
import com.hy.frame.bean.DownFile
import com.hy.frame.bean.ResultInfo
import com.hy.frame.ui.LoadingDialog
import com.hy.frame.util.MyLog
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 网络请求，不能直接使用<br></br>
 * 只能继承
 * @author HeYan
 * @time 2014年8月9日 下午3:43:44
 */
abstract class MyHttpClient constructor(val context: Context, listener: IMyHttpListener) {
    private var listeners: MutableList<IMyHttpListener>? = null
    var showDialog: Boolean = false// 显示加载对话框
    protected var loadingDialog: LoadingDialog? = null
        get() {
            if (field == null)
                field = LoadingDialog(context)
            return field
        }

    private var headerParams: MutableMap<String, String>? = null //默认头信息
    protected var isDestroy: Boolean = false
    private var client: OkHttpClient? = null
        get() {
            if (field == null) {
                val builder = OkHttpClient.Builder()
                if (!cerName.isNullOrEmpty()) {
                    val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .cipherSuites(
                                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                            .build()
                    builder.connectionSpecs(Collections.singletonList(spec))
                }
                builder.writeTimeout(writeTimeout, TimeUnit.SECONDS)
                builder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
                builder.readTimeout(readTimeout, TimeUnit.SECONDS)
                field = builder.build()
            }
            return field
        }
    private var queues: MutableMap<Int, okhttp3.Call>? = null
    private val handler: Handler = Handler(Looper.getMainLooper())

    private var cerName: String? = null//https签名证书name
    var writeTimeout = TIMEOUT_WRITE
    var connectTimeout = TIMEOUT_CONNECT
    var readTimeout = TIMEOUT_READ

    init {
        addListener(listener)
//        if (false) {
//            val pi = HyUtil.getAppVersion(context)
//            if (pi != null) {
//                if (pi.signatures != null && pi.signatures.isNotEmpty())
//                    addHeader("signatures", pi.signatures[0].toCharsString())
//            }
//        }
    }

    /**
     * set Listener
     */
    fun setListener(listener: IMyHttpListener) {
        if (this.listeners != null)
            this.listeners!!.clear()
        addListener(listener)
    }

    /**
     * add Listener
     */
    fun addListener(listener: IMyHttpListener) {
        if (this.listeners == null)
            this.listeners = ArrayList()
        this.listeners!!.add(listener)
    }

    private fun addQueue(requestCode: Int, call: okhttp3.Call?) {
        if (call == null) return
        if (this.queues == null)
            this.queues = ConcurrentHashMap()
        this.queues?.put(requestCode, call)
    }

    fun hasQueue(requestCode: Int): Boolean {
        return queues?.containsKey(requestCode) ?: false
    }

    private fun removeQueue(requestCode: Int): Boolean {
        if (queues != null) {
            queues?.remove(requestCode)
            return true
        }
        return false
    }

    /**
     * 添加默认头信息 User-Agent|Content-Type|Accept 无效
     * @param key
     * @param value
     */
    fun addHeader(key: String, value: String?) {
        if (headerParams == null) headerParams = ConcurrentHashMap()
        if (value == null) {
            if (headerParams != null)
                headerParams!!.remove(key)
        } else {
            if (headerParams == null)
                headerParams = ConcurrentHashMap()
            headerParams!!.put(key, value)
        }
    }

    @JvmOverloads
    fun <T> get(@StringRes requestCode: Int, params: AjaxParams? = null, cls: Class<T>? = null, list: Boolean = false, url: String? = null) {
        request(RequestMethod.GET, requestCode, params, cls, list, if (url.isNullOrEmpty()) getPath(requestCode) else url!!)
    }

    @JvmOverloads
    fun <T> post(@StringRes requestCode: Int, params: AjaxParams? = null, cls: Class<T>? = null, list: Boolean = false, url: String? = null) {
        request(RequestMethod.POST, requestCode, params, cls, list, if (url.isNullOrEmpty()) getPath(requestCode) else url!!)
    }

    /**
     * 请求</泛型>
     * @param method      请求方式
     * @param requestCode 请求码
     * @param params      请求参数
     * @param cls         类<泛型> (例如：Version.class) 如果为空则直接返回json字符串
     * @param list        结果是否是List
     * @param url         请求地址
     */
    open fun <T> request(method: RequestMethod, requestCode: Int, params: AjaxParams?, cls: Class<T>?, list: Boolean = false, url: String) {
        var requestUrl = url
        MyLog.d("request", requestUrl)
        val result = ResultInfo()
        result.requestCode = requestCode
        result.requestType = REQUEST_TYPE_JSON
        if (params != null) {
            if (params.getUrlParams() != null && params.getUrlParams()!!.isNotEmpty()) {
                MyLog.d("params", params.toString())
                if (method == RequestMethod.GET)
                    requestUrl = url.plus("?").plus(params.urlParamsString)
            }
            result.qid = params.getQid()
        }
        request(method, requestUrl, buildBody(method, params), result, cls, list, params?.getHeaderParams())
    }

    private fun buildBody(method: RequestMethod, params: AjaxParams?): RequestBody? {
        if (method == RequestMethod.GET || params == null) return null
        if (params.getFileParams() != null && params.getFileParams()!!.isNotEmpty()) {
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            if (params.getUrlParams() != null && params.getUrlParams()!!.isNotEmpty())
                for ((key, value) in params.getUrlParams()!!) {
                    builder.addFormDataPart(key, value)
                }
            for ((key, value) in params.getFileParams()!!) {
                val body = RequestBody.create(MediaType.parse(value.mimeType), value.file)
                //return body;
                builder.addPart(body)
                builder.addFormDataPart(key, value.fileName, body)
            }
            return builder.build()
        } else if (params.getUrlParams() != null && params.getUrlParams()!!.isNotEmpty()) {
            val builder = FormBody.Builder()
            for ((key, value) in params.getUrlParams()!!) {
                builder.add(key, value)
            }
            return builder.build()
        }
        return null
    }

    @MainThread
    fun <T> request(method: RequestMethod, url: String, body: RequestBody?, result: ResultInfo, cls: Class<T>?, list: Boolean, headers: Map<String, String>? = null) {
        if (isDestroy) return
        if (hasQueue(result.requestCode)) {
            MyLog.e("request", "what=" + result.requestCode + ",msg=" + getString(R.string.API_FLAG_REPEAT))
            val call = queues?.get(result.requestCode) ?: return
            if (!call.isCanceled)
                return
            removeQueue(result.requestCode)
        }
        val builder = Request.Builder().url(url)
        builder.method(method.toString(), body)
        builder.tag(result)
        val sb = StringBuilder()
        if (headerParams != null) {
            for ((key, value) in headerParams!!) {
                builder.addHeader(key, value)
                sb.append("$key=$value&")
            }
        }
        if (headers != null) {
            for ((key, value) in headers) {
                builder.addHeader(key, value)
                sb.append("$key=$value&")
            }
        }
        if (sb.isNotEmpty())
            MyLog.d("headers", sb.toString().dropLast(1))
        when (result.requestType) {
            REQUEST_TYPE_JSON, REQUEST_TYPE_JSONARRAY -> builder.addHeader(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_ACCEPT_JSON)
            REQUEST_TYPE_STRING -> builder.addHeader(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_ACCEPT_STRING)
            REQUEST_TYPE_FILE -> builder.addHeader(Headers.HEAD_KEY_ACCEPT, Headers.HEAD_ACCEPT_FILE)
        }
        showLoading()
        val request: Request = builder.build()
        //MyLog.d("Request", "headers=" + request.headers().toString())
        val call = client?.newCall(request)
        addQueue(result.requestCode, call)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException?) {
                val r = call.request().tag() as ResultInfo
                val msg = e?.message
                MyLog.e("onFailed", "what=" + result.requestCode + ",msg=" + msg)
                var code = R.string.API_FLAG_CON_EXCEPTION
                r.errorCode = ResultInfo.CODE_ERROR_NET
                if (msg != null) {
                    val thr = msg.toLowerCase(Locale.CHINA)
                    when {
                        thr.contains("broken pipe") -> code = R.string.API_FLAG_CON_BROKEN
                        thr.contains("timed out") -> code = R.string.API_FLAG_CON_TIMEOUT
                        thr.contains("unknownhostexception") -> code = R.string.API_FLAG_CON_UNKNOWNHOSTEXCEPTION
                    }
                }
                r.msg = getString(code)
                onRequestError(r)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (isDestroy) return
                //MyLog.d("onResponse", "headers=" + response.headers().toString())
                //hideLoading();
                val r = call.request().tag() as ResultInfo
                if (response.isSuccessful) {
                    if (response.headers() != null && response.headers().size() > 0) {
                        for (i in 0 until response.headers().size())
                            r.putValue(response.headers().name(i), response.headers().value(i))
                    }
                    if (r.requestType == REQUEST_TYPE_FILE) {
                        doSuccessFile(r, response.body())
                        return
                    }
                    var data = response.body()!!.string()
                    MyLog.d("onSucceed", "what=" + r.requestCode + ",data=" + data)
                    if (data.isNotEmpty()) {
                        try {
                            data = data.replace(":\\[]".toRegex(), ":null")
                            data = data.replace(":\"\"".toRegex(), ":null")
                            when (r.requestType) {
                                REQUEST_TYPE_JSON -> {
                                    doSuccess(r, JsonParser().parse(data).asJsonObject, cls, list)
                                    return
                                }
                                REQUEST_TYPE_JSONARRAY -> {
                                    doSuccess(r, JsonParser().parse(data).asJsonArray, cls, list)
                                    return
                                }
                                REQUEST_TYPE_STRING -> {
                                    doSuccess(r, data, cls, list)
                                    return
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    MyLog.i("onFail", "what=" + r.requestCode + ",data=" + response.message())
                }
                r.errorCode = ResultInfo.CODE_ERROR_DEFAULT
                r.msg = getString(R.string.API_FLAG_NO_RESPONSE)
                onRequestError(r)
            }
        })
    }

    fun download(requestCode: Int, url: String, fileFolder: String, fileName: String, isRange: Boolean, isDeleteOld: Boolean, qid: Long = 0) {
        MyLog.i("download", url)
        val result = ResultInfo()
        result.requestCode = requestCode
        result.requestType = REQUEST_TYPE_FILE
        result.qid = qid
        val method = RequestMethod.GET

        val downFile = DownFile()
        downFile.saveDir = fileFolder
        downFile.isRange = isRange
        downFile.url = url
        downFile.fileName = fileName
        downFile.isDeleteOld = isDeleteOld
        result.setObj(downFile)
        request<Any>(method, url, null, result, null, false)
    }

    private fun doSuccessFile(result: ResultInfo, body: ResponseBody?) {
        MyLog.d("doSuccessFile" + result.requestCode)
        if (body == null) {
            onRequestSuccess(result)
            return
        }
        val downFile: DownFile = result.getObj<DownFile>() ?: return
        val total = body.contentLength()
        MyLog.d("onProgress(File)", "what=" + result.requestCode + ",total=" + total)
        downFile.state = DownFile.STATUS_START
        downFile.allCount = total
        onRequestSuccess(result)
        var input: InputStream? = null
        var fos: FileOutputStream? = null
        val buf = ByteArray(2048)
        try {
            input = body.byteStream()
            val cacheFile = File(downFile.saveDir, downFile.fileName!! + ".cache")
            fos = FileOutputStream(cacheFile)
            var sum: Long = 0
            var length = input.read(buf)
            while (length != -1) {
                fos.write(buf, 0, length)
                sum += length.toLong()
                val progress = (sum * 1.0f / total * 100).toInt()
                MyLog.d("onProgress(File)", "what=" + result.requestCode + ",progress=" + progress + ",fileCount=" + sum + ",total=" + total)
                downFile.state = DownFile.STATUS_PROGRESS
                downFile.progress = progress
                downFile.fileCount = sum
                result.setObj(downFile)
                onRequestSuccess(result)
                length = input.read(buf)
            }
            fos.flush()
            if (total > 0 && sum == total) {
                downFile.state = DownFile.STATUS_SUCCESS
                val file = File(downFile.saveDir, downFile.fileName!!)
                cacheFile.renameTo(file)
                MyLog.i("onSucceed", "文件下载成功")
            } else {
                downFile.state = DownFile.STATUS_ERROR
                MyLog.i("onSucceed", "文件下载中断")
            }
        } catch (e: Exception) {
            MyLog.i("onSucceed", "文件下载失败")
            downFile.state = DownFile.STATUS_ERROR
        } finally {
            try {
                if (input != null)
                    input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                if (fos != null)
                    fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        result.setObj(downFile)
        onRequestSuccess(result)
    }

    /**
     * String 格式数据(如果要使用请重构)
     * @param result
     * @param json
     * @param cls
     * @param list
     * @param <T>
     */
    protected open fun <T> doSuccess(result: ResultInfo, json: String, cls: Class<T>?, list: Boolean) {
        result.setObj(json)
        onRequestSuccess(result)
    }

    /**
     * JSONArray 格式数据
     * @param result
     * @param obj
     * @param cls
     * @param list
     * @param <T>
     */
    protected open fun <T> doSuccess(result: ResultInfo, obj: JsonArray, cls: Class<T>?, list: Boolean) {
        result.setObj(obj)
        onRequestSuccess(result)
    }

    /**
     * JSONObject 格式数据 (默认)
     * @param result
     * @param obj
     * @param cls
     * @param list
     * @param <T>
     */
    protected open fun <T> doSuccess(result: ResultInfo, obj: JsonObject, cls: Class<T>?, list: Boolean) {}

    /**
     * not main thread
     */
    protected fun onRequestSuccess(result: ResultInfo) {
        if (isDestroy) return
        result.errorCode = 0
        runnable?.isSuccess = true
        runnable?.result = result
        handler.post(runnable)
    }

    /**
     * not main thread
     */
    protected fun onRequestError(result: ResultInfo) {
        if (isDestroy) return
        runnable?.isSuccess = false
        runnable?.result = result
        handler.post(runnable)
    }

    private var runnable: MyRunnable? = null
        get() {
            if (field == null) {
                field = object : MyRunnable() {
                    override fun run() {
                        handler.removeCallbacks(runnable)
                        if (result == null) return
                        val r: ResultInfo = result!!
                        MyLog.d("onRequest run" + r.requestCode)
                        if (!hasQueue(r.requestCode)) return
                        if (r.requestType == REQUEST_TYPE_FILE) {
                            val downFile = r.getObj<DownFile>()
                            if (downFile?.state == DownFile.STATUS_SUCCESS || downFile?.state == DownFile.STATUS_ERROR) {
                                removeQueue(r.requestCode)
                                hideLoading()
                            }
                        } else {
                            removeQueue(r.requestCode)
                            hideLoading()
                        }
                        if (listeners != null) {
                            for (listener in listeners!!) {
                                if (isSuccess)
                                    listener.onRequestSuccess(r)
                                else
                                    listener.onRequestError(r)
                            }
                        }
                    }
                }
            }
            return field
        }


    /**
     * 显示加载框
     */
    @UiThread
    protected fun showLoading(loadMsg: String? = null) {
        if (isDestroy) return
        if (showDialog) {
            if (!loadMsg.isNullOrEmpty())
                loadingDialog?.updateMsg(loadMsg!!)
            loadingDialog?.show()
        }
    }

    /**
     * 隐藏加载框
     */
    @UiThread
    protected fun hideLoading() {
        if (isDestroy) return
        loadingDialog?.dismiss()
    }

    @UiThread
    protected fun updateLoadingMsg(loadMsg: String) {
        if (isDestroy) return
        loadingDialog?.updateMsg(loadMsg)
    }

    /**
     * 销毁，释放
     */
    @MainThread
    fun onDestroy() {
        //在这里销毁所有当前请求
        MyLog.d(javaClass, "onDestroy")
        isDestroy = true
        listeners = null
        loadingDialog = null
        if (queues != null && queues!!.isNotEmpty())
            for ((_, value) in queues!!) {
                if (!value.isCanceled)
                    value.cancel()
            }
        queues = null
    }

    abstract fun getPath(resId: Int): String

    protected fun getString(resId: Int): String {
        return context.getString(resId)
    }

    private open inner class MyRunnable : Runnable {
        var result: ResultInfo? = null
        var isSuccess: Boolean = false
        override fun run() {}
    }

    companion object {
        val REQUEST_TYPE_JSON = 0
        val REQUEST_TYPE_JSONARRAY = 1
        val REQUEST_TYPE_STRING = 2
        val REQUEST_TYPE_FILE = 3
        val TIMEOUT_WRITE = 30L
        val TIMEOUT_CONNECT = 30L
        val TIMEOUT_READ = 30L
    }
}
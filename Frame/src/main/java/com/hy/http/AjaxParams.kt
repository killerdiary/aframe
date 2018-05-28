package com.hy.http

import com.hy.http.file.Binary
import com.hy.http.file.FileBinary
import java.util.concurrent.ConcurrentHashMap

/**
 * AjaxParams for key/value (include Multipart file)
 * @author HeYan
 * @time 2017/5/8 15:20
 */
class AjaxParams {
    //private static final String ENCODING = "UTF-8";
    private var qid: Long = 0L
    private var urlParams: MutableMap<String, String>? = null
    private var headerParams: MutableMap<String, String>? = null
    private var fileParams: MutableMap<String, Binary>? = null

    constructor()

    constructor(key: String, value: String) {
        put(key, value)
    }

//    /**
//     * Add [Object] param.
//     * @param key   param name.
//     * @param value param value.
//     */
//    fun put(key: String, value: Any) {
//        put(key, value.toString())
//    }

    /**
     * Add [String] param.
     * @param key   param name.
     * @param value param value.
     */
    fun put(key: String, value: String?): AjaxParams {
        if (urlParams == null) urlParams = ConcurrentHashMap()
        if (value == null) {
            if (urlParams != null)
                urlParams!!.remove(key)
        } else {
            if (urlParams == null)
                urlParams = ConcurrentHashMap()
            urlParams!!.put(key, value)
        }
        return this
    }

    /**
     * Add [Integer] param.
     * @param key   param name.
     * @param value param value.
     */
    fun put(key: String, value: Int): AjaxParams {
        put(key, value.toString())
        return this
    }

    /**
     * Add [Long] param.
     * @param key   param name.
     * @param value param value.
     */
    fun put(key: String, value: Long): AjaxParams {
        put(key, value.toString())
        return this
    }

    /**
     * Add [Double] param.
     * @param key   param name.
     * @param value param value.
     */
    fun put(key: String, value: Double): AjaxParams {
        put(key, value.toString())
        return this
    }

    /**
     * Add [Float] param.
     * @param key   param name.
     * @param value param value.
     */
    fun put(key: String, value: Float): AjaxParams {
        put(key, value.toString())
        return this
    }

    /**
     * Add [Boolean] param.
     * @param key   param name.
     * @param value param value.
     */
    fun put(key: String, value: Boolean): AjaxParams {
        put(key, value.toString())
        return this
    }

    /**
     * Add [String] Header param.
     * @param key   param name.
     * @param value param value.
     */
    fun putHeader(key: String, value: String?): AjaxParams {
        if (headerParams == null) headerParams = ConcurrentHashMap()
        if (value == null) {
            if (headerParams != null)
                headerParams!!.remove(key)
        } else {
            if (headerParams == null)
                headerParams = ConcurrentHashMap()
            headerParams!!.put(key, value)
        }
        return this
    }

    /**
     * Add [FileBinary] param.
     * @param key  param name.
     * @param file param value.
     */
    fun put(key: String, file: FileBinary): AjaxParams {
        if (fileParams == null)
            fileParams = ConcurrentHashMap<String, Binary>()
        fileParams!!.put(key, file)
        return this
    }

    fun setQid(qid: Long): AjaxParams {
        this.qid = qid
        return this
    }

    fun getQid(): Long {
        return qid
    }

    fun getUrlParams(): Map<String, String>? {
        return urlParams
    }

    fun getHeaderParams(): Map<String, String>? {
        return headerParams
    }


    fun getFileParams(): Map<String, Binary>? {
        return fileParams
    }

    val urlParamsString: String
        get() {
            val sb = StringBuilder()
            if (urlParams != null && urlParams!!.isNotEmpty()) {
                val size = urlParams!!.size
                var i = 0
                for ((key, value) in urlParams!!) {
                    i++
                    sb.append(key)
                    sb.append("=")
                    sb.append(value)
                    if (i < size)
                        sb.append("&")
                }
            }
            return sb.toString()
        }

    val headerParamsString: String
        get() {
            val sb = StringBuilder()
            if (headerParams != null && headerParams!!.isNotEmpty()) {
                val size = headerParams!!.size
                var i = 0
                for ((key, value) in headerParams!!) {
                    i++
                    sb.append(key)
                    sb.append("=")
                    sb.append(value)
                    if (i < size)
                        sb.append("&")
                }
            }
            return sb.toString()
        }

    val fileParamsString: String?
        get() {
            val sb = StringBuilder()
            if (fileParams != null && fileParams!!.isNotEmpty()) {
                val size = fileParams!!.size
                var i = 0
                for ((key, value) in fileParams!!) {
                    i++
                    sb.append(key)
                    sb.append("=")
                    sb.append(value)
                    if (i < size)
                        sb.append("&")
                }
            }
            return sb.toString()
        }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(urlParamsString)
        val fileParamsString = fileParamsString
        if (sb.isNotEmpty() && fileParamsString != null && fileParamsString.isNotEmpty()) {
            sb.append("&")
            sb.append(fileParamsString)
        }
        return sb.toString()
    }
}
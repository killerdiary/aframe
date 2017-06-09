package com.hy.frame.bean

import java.util.*

/**
 * ResultInfo 服务器返回数据
 * @author HeYan
 * @time 2017/5/23 10:51
 */
class ResultInfo {
    /**
     * 请求码，接口编号
     */
    /**
     * 请求码，接口编号
     */
    var requestCode: Int = 0//请求码，接口编号
    /**
     * 队列ID
     */
    /**
     * 队列ID
     */
    var qid: Long = 0//队列ID
    private var obj: Any? = null//返回结果
    /**
     * 错误码 250 本地错误 251 网络错误
     */
    /**
     * 错误码
     */
    var errorCode: Int = 0//错误码
    /**
     * 描述
     */
    /**
     * 描述
     */
    var msg: String? = null//描述
    var requestType: Int = 0
    private var maps: MutableMap<String, String>? = null//其他


    /**
     * 返回结果
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getObj(): T {
        return obj as T
    }

    /**
     * 返回结果
     */
    fun setObj(obj: Any?) {
        this.obj = obj
    }

    fun putValue(key: String, value: String) {
        if (maps == null)
            maps = HashMap<String, String>()
        maps!!.put(key, value)
    }

    fun getValue(key: String): String? {
        if (maps != null) return maps!![key]
        return null
    }

    fun getMaps(): MutableMap<String, String>? = maps

    companion object {
        /**
         * 本地错误 默认
         */
        val CODE_ERROR_DEFAULT = -250
        /**
         * 网络错误
         */
        val CODE_ERROR_NET = -251
        /**
         * 解析错误
         */
        val CODE_ERROR_DECODE = -252
    }
}

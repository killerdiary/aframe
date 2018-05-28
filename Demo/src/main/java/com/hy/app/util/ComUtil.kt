package com.hy.app.util

import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.hy.app.R
import com.hy.frame.util.MyLog
import java.util.*

/**
 * 工具类
 *
 * @author HeYan
 * @time 2014-7-23 下午1:57:31
 */
object ComUtil {
    fun displayImage(requestManager: RequestManager?, imageView: ImageView?, url: String?) {
        display(requestManager, imageView, url, R.drawable.def_empty, R.drawable.def_empty)
    }

    fun displayHead(requestManager: RequestManager?, imageView: ImageView?, url: String?) {
        display(requestManager, imageView, url, R.drawable.def_empty, R.drawable.def_empty)
    }

    private fun display(requestManager: RequestManager?, imageView: ImageView?, url: String?, loading: Int, fail: Int, isGif: Boolean = false) {
        if (requestManager == null || imageView == null || url.isNullOrEmpty()) {
            imageView?.setImageResource(fail)
            return
        }
        MyLog.d("display", url!!)
        try {
            var builder = if (isGif) requestManager.asGif() else requestManager.asBitmap()
            builder = builder.load(url)
            val options = RequestOptions.noTransformation().placeholder(loading).error(fail)
            builder = builder.apply(options)
            builder.into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getStringFromJson(json: JsonObject?, key: String): String? {
        if (json == null || !json.has(key) || json.get(key).isJsonNull) return null
        return json.get(key).asString
    }

    fun getIntFromJson(json: JsonObject?, key: String): Int {
        if (json == null || !json.has(key) || json.get(key).isJsonNull) return 0
        return json.get(key).asInt
    }

    fun getBooleanFromJson(json: JsonObject?, key: String): Boolean {
        if (json == null || !json.has(key) || json.get(key).isJsonNull) return false
        return json.get(key).asBoolean
    }

    fun <T> getListFromJson(json: JsonObject?, key: String, cls: Class<T>): MutableList<T>? {
        return getListFromJson(json?.get(key), cls)
    }

    fun <T> getListFromJson(data: JsonElement?, cls: Class<T>): MutableList<T>? {
        if (data == null || !data.isJsonArray) return null
        return try {
            val beans: Array<T> = Gson().fromJson(data, TypeToken.getArray(cls).type)
            val items = ArrayList(Arrays.asList(*beans))
            items
        } catch (e: Exception) {
            null
        }
    }

    fun <T> getObjectFromJson(data: JsonElement?, cls: Class<T>): T? {
        if (data == null || !data.isJsonObject) return null
        return try {
            Gson().fromJson(data, cls)
        } catch (e: Exception) {
            null
        }
    }
}
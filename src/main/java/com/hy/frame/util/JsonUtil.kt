package com.hy.frame.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken


/**
 * Json Util
 *
 * @author HeYan
 * @time 2018/4/6 12:03
 */
object JsonUtil {
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
            val items = ArrayList(java.util.Arrays.asList(*beans))
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
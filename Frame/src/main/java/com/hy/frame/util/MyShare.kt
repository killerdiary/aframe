package com.hy.frame.util

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences工具
 * @author HeYan
 * @time 2014年12月17日 下午5:47:46
 */
open class MyShare constructor(context: Context, shareName: String = MyShare.SHARE_DEFAULT) {
    private val share: SharedPreferences

    init {
        share = getShared(context, shareName)
    }

    private fun getShared(context: Context, shareName: String): SharedPreferences {
        return context.applicationContext.getSharedPreferences(shareName,
                Context.MODE_PRIVATE)
    }

    fun getString(key: String): String? {
        return share.getString(key, null)
    }

    fun getInt(key: String): Int {
        return share.getInt(key, 0)
    }

    fun getLong(key: String): Long {
        return share.getLong(key, 0)
    }

    fun getFloat(key: String): Float {
        return share.getFloat(key, 0f)
    }

    fun getBoolean(key: String): Boolean {
        return share.getBoolean(key, false)
    }

    operator fun contains(key: String): Boolean {
        return share.contains(key)
    }

    fun putString(key: String, value: String?) {
        share.edit().putString(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        share.edit().putInt(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        share.edit().putLong(key, value).apply()
    }

    fun putFloat(key: String, value: Float) {

        share.edit().putFloat(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        share.edit().putBoolean(key, value).apply()
    }

    fun remove(key: String) {
        share.edit().remove(key).apply()
    }

    fun clear() {
        share.edit().clear().apply()
    }

    companion object {
        private var instance: MyShare? = null
        val SHARE_DEFAULT = "SHARE_DEFAULT"

        /**
         * 获取实例
         */
        operator fun get(context: Context): MyShare {
            if (instance == null)
                instance = MyShare(context)
            return instance!!
        }
    }

}

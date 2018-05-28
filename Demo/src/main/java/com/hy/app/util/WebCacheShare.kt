package com.hy.app.util

import android.content.Context

import com.hy.frame.util.MyShare

/**
 * 网站缓存工具
 */
class WebCacheShare(context: Context) : MyShare(context, SHARE_CACHE) {

    init {
        clearCache()
    }

    /**
     * 相隔一天则清理
     */
    private fun clearCache() {
        val cur = System.currentTimeMillis()
        val last = getLong(LAST_TIME)
        val split = ((cur - last) / (24 * 60 * 60 * 1000L))
        if (split > 0) {
            clear()
        }
    }

    companion object {
        val SHARE_CACHE = "SHARE_CACHE"
        val LAST_TIME = "LAST_TIME"
        private var instance: MyShare? = null

        /**
         * 获取实例
         */
        operator fun get(context: Context): MyShare {
            if (instance == null)
                instance = WebCacheShare(context)
            return instance!!
        }
    }
}

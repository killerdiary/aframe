package com.hy.frame.util;

import android.content.Context;

/**
 * @author HY
 * @title 缓存SHARE
 * @time 2015-5-20 上午11:31:46
 */
@Deprecated
public class CacheShare extends MyShare {
    public static final String SHARE_CACHE = "SHARE_CACHE";
    public static final String LAST_TIME = "LAST_TIME";
    private static MyShare instance;

    public CacheShare(Context context) {
        super(context, SHARE_CACHE);
        clearCache();
    }

    /**
     * 获取实例
     */
    public static MyShare get(Context context) {
        if (instance == null)
            instance = new CacheShare(context);
        return instance;
    }

    /**
     * 相隔一天则清理
     */
    private void clearCache() {
        long cur = System.currentTimeMillis();
        long last = getLong(LAST_TIME);
        long split = (long) ((cur - last) / (24 * 60 * 60 * 1000));
        if (split > 0) {
            clear();
        }
    }
}

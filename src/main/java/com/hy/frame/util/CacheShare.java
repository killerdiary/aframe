package com.hy.frame.util;

import android.content.Context;

/**
 * @title 缓存SHARE
 * @author HY
 * @time 2015-5-20 上午11:31:46
 */
@Deprecated
public class CacheShare extends MyShare {
    private static CacheShare instance;
    public static final String LAST_TIME = "LAST_TIME";

    public CacheShare(Context context) {
        super(context, true);
        clearCache();
    }

    /**
     * 获取实例
     */
    public static CacheShare get(Context context) {
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

package com.hy.frame.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具
 *
 * @author HeYan
 * @time 2014年12月17日 下午5:47:46
 */
public class MyShare {
    private static MyShare instance;
    private SharedPreferences share;
    public static final String SHARE_DEFAULT = "default";
    public static final String SHARE_CACHE = "cache";

    //    private SharedPreferences  getShare(Context context, boolean cache) {
//        if (cache)
//            share = context.getSharedPreferences(SHARE_CACHE,
//                    Context.MODE_PRIVATE);
//        else
//            share = context.getSharedPreferences(SHARE_DEFAULT,
//                    Context.MODE_PRIVATE);
//    }
//


    /**
     * 获取实例
     */
    public static MyShare get(Context context) {
        if (instance == null)
            instance = new MyShare(context, false);
        return instance;
    }

    public MyShare(Context context) {
        this(context, false);
    }

    public MyShare(Context context, boolean cache) {
        if (cache)
            share = context.getSharedPreferences(SHARE_CACHE,
                    Context.MODE_PRIVATE);
        else
            share = context.getSharedPreferences(SHARE_DEFAULT,
                    Context.MODE_PRIVATE);
    }

    private SharedPreferences getShared(Context context, boolean cache) {
        if (cache)
            return context.getSharedPreferences(SHARE_CACHE,
                    Context.MODE_PRIVATE);
        return context.getSharedPreferences(SHARE_DEFAULT,
                Context.MODE_PRIVATE);
    }

    public String getString(String key) {
        return share.getString(key, null);
    }

    public int getInt(String key) {
        return share.getInt(key, 0);
    }

    public long getLong(String key) {
        return share.getLong(key, 0);
    }

    public float getFloat(String key) {
        return share.getFloat(key, 0);
    }

    public boolean getBoolean(String key) {
        return share.getBoolean(key, false);
    }

    public boolean contains(String key) {
        return share.contains(key);
    }

    public void putString(String key, String value) {
        share.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        share.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        share.edit().putLong(key, value).apply();
    }

    public void putFloat(String key, float value) {

        share.edit().putFloat(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {

        share.edit().putBoolean(key, value).apply();
    }

    public void remove(String key) {
        share.edit().remove(key).apply();
    }

    public void clear() {
        share.edit().clear().apply();
    }

}

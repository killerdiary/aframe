package com.hy.frame.util;

import android.util.Log;

/**
 * 日志
 * 
 * @author HeYan
 * @time 2014年12月17日 下午4:05:40
 */
public class MyLog {
    private final static String TAG = "HyLog";

    public static void i(Object msg) {
        i(TAG, msg);
    }

    public static void i(Class<?> cls, Object msg) {
        i(cls.getSimpleName(), msg);
    }

    public static void i(String tag, Object msg) {
        Log.i(tag, "" + msg);
    }

    public static void d(Object msg) {
        d(TAG, msg);
    }

    public static void d(Class<?> cls, Object msg) {
        d(cls.getSimpleName(), msg);
    }

    public static void d(String tag, Object msg) {
        Log.d(tag, "" + msg);
    }

    public static void e(Object msg) {
        e(TAG, msg);
    }

    public static void e(Class<?> cls, Object msg) {
        e(cls.getSimpleName(), msg);
    }

    public static void e(String tag, Object msg) {
        Log.e(tag, "" + msg);
    }
}

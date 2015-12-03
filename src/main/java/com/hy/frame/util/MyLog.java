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
    public static final boolean isLoggable = true;
    //public static final boolean isLoggable = Log.isLoggable(TAG, Log.VERBOSE);

    public static void i(Object msg) {
        i("", msg);
    }

    public static void i(Class<?> cls, Object msg) {
        i(cls.getSimpleName(), msg);
    }

    public static void i(String tag, Object msg) {
        println(Log.INFO, tag, msg, null);
    }

    public static void d(Object msg) {
        d("", msg);
    }

    public static void d(Class<?> cls, Object msg) {
        d(cls.getSimpleName(), msg);
    }

    public static void d(String tag, Object msg) {
        println(Log.DEBUG, tag, msg, null);
    }

    public static void w(Object msg) {
        w("", msg);
    }

    public static void w(Class<?> cls, Object msg) {
        w(cls.getSimpleName(), msg);
    }

    public static void w(String tag, Object msg) {
        println(Log.WARN, tag, msg, null);
    }

    public static void e(Object msg) {
        e("", msg);
    }

    public static void e(Class<?> cls, Object msg) {
        e(cls.getSimpleName(), msg);
    }

    public static void e(String tag, Object msg) {
        println(Log.ERROR, tag, msg, null);
    }

    private static void println(int priority, String tag, Object msg, Throwable tr) {
        if (isLoggable)
            Log.println(priority, TAG, HyUtil.isEmpty(tag) ? "" : tag + ": " + msg);
    }
}

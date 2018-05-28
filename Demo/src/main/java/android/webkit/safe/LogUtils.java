package android.webkit.safe;

import android.util.Log;

import com.hy.app.BuildConfig;

/**
 * LogUtils ,Created by zhangguojun on 2017/3/27.
 * @author HeYan
 * @time 2017/9/19 11:20
 */
public class LogUtils {

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static void safeCheckCrash(String tag, String msg, Throwable tr) {
        if (isDebug()) {
            throw new RuntimeException(tag + " " + msg, tr);
        } else {
            Log.e(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }
}

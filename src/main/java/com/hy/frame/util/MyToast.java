package com.hy.frame.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * 自定义Toast
 *
 * @author HeYan
 * @time 2014-7-21 上午9:44:27
 */
public class MyToast {
    private static Toast toast;

    public static void show(Context context, @StringRes int msgId) {
        show(context, context.getResources().getString(msgId));
    }

    public static void show(Context context, String msg) {
        try {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            //toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            if (MyLog.INSTANCE.getIsLoggable())
                e.printStackTrace();
        }
    }

    public static void show(Context context, View v) {
        try {
            if (toast != null)
                toast.cancel();
            toast = new Toast(context);
            toast.setView(v);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(v);
            toast.show();
        } catch (Exception e) {
            if (MyLog.INSTANCE.getIsLoggable())
                e.printStackTrace();
        }
    }
}
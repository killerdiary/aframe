package com.hy.frame.util;

import android.content.Context;
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

    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//        Toast toast = new Toast(context);
//        View v = LayoutInflater.from(context).inflate(R.layout.toast, null);
//        TextView txtMsg = (TextView) v.findViewById(R.id.txtMsg);
//        txtMsg.setText(msg == null ? "错误" : msg);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setView(v);
//        toast.show();
    }

    public static void show(Context context, View v) {
        Toast toast = new Toast(context);
        toast.setView(v);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(v);
        toast.show();
    }
}
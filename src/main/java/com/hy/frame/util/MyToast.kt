package com.hy.frame.util

import android.content.Context
import android.support.annotation.StringRes
import android.view.Gravity
import android.view.View
import android.widget.Toast

/**
 * 自定义Toast

 * @author HeYan
 *
 * @time 2014-7-21 上午9:44:27
 */
object MyToast {
    private var toast: Toast? = null

    fun show(context: Context, @StringRes msgId: Int) {
        show(context, context.resources.getString(msgId))
    }

    fun show(context: Context, msg: String) {
        try {
            if (toast != null)
                toast!!.cancel()
            toast = Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT)
            //toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
            toast!!.show()
        } catch (e: Exception) {
            if (MyLog.isLoggable)
                e.printStackTrace()
        }
    }

    fun show(context: Context, v: View) {
        try {
            if (toast != null)
                toast!!.cancel()
            toast = Toast(context)
            toast!!.view = v
            toast!!.setGravity(Gravity.CENTER, 0, 0)
            toast!!.duration = Toast.LENGTH_SHORT
            toast!!.view = v
            toast!!.show()
        } catch (e: Exception) {
            if (MyLog.isLoggable)
                e.printStackTrace()
        }

    }
}
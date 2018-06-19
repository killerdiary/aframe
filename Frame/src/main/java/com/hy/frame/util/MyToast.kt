package com.hy.frame.util

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hy.frame.R

/**
 * 自定义Toast
 * @author HeYan
 * @time 2014-7-21 上午9:44:27
 */
object MyToast {
    private var toast: Toast? = null

    fun show(context: Context, @StringRes msgId: Int) {
        show(context, context.resources.getString(msgId))
    }

    fun show(context: Context, msg: String?) {
        try {
            if (toast != null)
                toast!!.cancel()
            //toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
            toast = Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT)
            toast!!.show()
        } catch (e: Exception) {
            if (MyLog.isLoggable)
                e.printStackTrace()
        }
    }

    fun showV(context: Context, msg: String?, @DrawableRes drawId: Int = R.drawable.v_warn) {
        try {
            if (toast != null)
                toast!!.cancel()
            val v = View.inflate(context, R.layout.v_toast, null)
            val txt = v.findViewById<TextView>(R.id.txtMsg)
            txt?.text = msg
            txt?.setCompoundDrawablesWithIntrinsicBounds(drawId, 0, 0, 0)
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

    fun showV(context: Context, v: View) {
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
package com.hy.frame.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup

object StatusBarUtil {
    private val INVALID_VAL = -1
    private val COLOR_DEFAULT = Color.parseColor("#0288D1")

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun changeStatusColor(activity: Activity, statusColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (statusColor != INVALID_VAL) {
                activity.window.statusBarColor = statusColor
            }
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            var color = COLOR_DEFAULT
            val contentView = activity.findViewById<View>(android.R.id.content) as ViewGroup
            if (statusColor != INVALID_VAL) {
                color = statusColor
            }
            var statusBarView: View? = contentView.getChildAt(0)
            //改变颜色时避免重复添加statusBarView
            if (statusBarView != null && statusBarView.measuredHeight == getStatusBarHeight(activity)) {
                statusBarView.setBackgroundColor(color)
                return
            }
            statusBarView = View(activity)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(activity))
            statusBarView.setBackgroundColor(color)
            contentView.addView(statusBarView, lp)
        }

    }

    fun changeStatusColor(activity: Activity) {
        changeStatusColor(activity, INVALID_VAL)
    }


    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}

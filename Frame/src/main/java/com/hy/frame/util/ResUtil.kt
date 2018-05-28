package com.hy.frame.util

import android.content.Context
import android.os.Build
import android.support.annotation.ColorRes
import android.util.DisplayMetrics
import android.util.TypedValue

/**
 * 常用的Dimension转换
 *
 * @author HeYan
 * @time 2018/4/6 11:14
 */
object ResUtil {

    /**
     * px to dimension
     */
    fun px2dimension(unit: Int, value: Float, metrics: DisplayMetrics): Float {
        return when (unit) {
            TypedValue.COMPLEX_UNIT_PX -> value
            TypedValue.COMPLEX_UNIT_DIP -> value / metrics.density
            TypedValue.COMPLEX_UNIT_SP -> value / metrics.scaledDensity
            TypedValue.COMPLEX_UNIT_PT -> value / (metrics.xdpi * (1.0f / 72))
            TypedValue.COMPLEX_UNIT_IN -> value / metrics.xdpi
            TypedValue.COMPLEX_UNIT_MM -> value / (metrics.xdpi * (1.0f / 25.4f))
            else -> 0f
        }
    }

    /**
     * dimension to px
     */
    fun dimension2px(unit: Int, value: Float, metrics: DisplayMetrics): Int {
        return Math.round(TypedValue.applyDimension(unit, value, metrics))
    }

    /**
     * px to sp
     */
    fun px2sp(context: Context, value: Float): Float {
        return px2dimension(TypedValue.COMPLEX_UNIT_SP, value, context.resources.displayMetrics)
    }

    /**
     * px to dip
     */
    fun px2dip(context: Context, value: Float): Float {
        return px2dimension(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics)
    }

    /**
     * sp to px
     */
    fun sp2px(context: Context, value: Float): Int {
        return dimension2px(TypedValue.COMPLEX_UNIT_SP, value, context.resources.displayMetrics)
    }

    /**
     * dip to px
     */
    fun dip2px(context: Context, value: Float): Int {
        return dimension2px(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics)
    }

    fun getColor(context: Context, @ColorRes resId: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getColor(resId, context.theme)
        } else {
            context.resources.getColor(resId)
        }
    }
}
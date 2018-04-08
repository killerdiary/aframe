package com.hy.frame.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

/**
 * 常用的Dimension转换
 *
 * @author HeYan
 * @time 2018/4/6 11:14
 */
object DimensionUtil {

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
    fun px2sp(value: Float, context: Context): Float {
        return px2dimension(TypedValue.COMPLEX_UNIT_SP, value, context.resources.displayMetrics)
    }

    /**
     * px to dip
     */
    fun px2dip(value: Float, context: Context): Float {
        return px2dimension(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics)
    }

    /**
     * sp to px
     */
    fun sp2px(value: Float, context: Context): Int {
        return dimension2px(TypedValue.COMPLEX_UNIT_SP, value, context.resources.displayMetrics)
    }

    /**
     * dip to px
     */
    fun dip2px(value: Float, context: Context): Int {
        return dimension2px(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics)
    }
}
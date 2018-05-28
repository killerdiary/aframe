package com.hy.app.ui.cliphead.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast

import com.hy.app.common.MyApplication


object UIUtils {

    /**
     * 全局上下文环境
     * @return
     */
    val context: Context
        get() = MyApplication.app!!

    /**
     * 获取屏幕宽度
     */
    val screenWidth: Int
        @SuppressLint("NewApi")
        get() {
            val wm = context.getSystemService(
                    Context.WINDOW_SERVICE) as WindowManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                val size = Point()
                wm.defaultDisplay.getSize(size)
                return size.x
            } else {
                val d = wm.defaultDisplay
                return d.width
            }
        }

    /**
     * 获取屏幕高度
     */
    val screenHeight: Int
        @SuppressLint("NewApi")
        get() {
            val wm = context.getSystemService(
                    Context.WINDOW_SERVICE) as WindowManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                val size = Point()
                wm.defaultDisplay.getSize(size)
                return size.y
            } else {
                val d = wm.defaultDisplay
                return d.height
            }
        }

    /**
     * dp转px
     *
     * @param dip
     * @return
     */
    fun dip2px(dip: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dip * scale + 0.5f).toInt()
    }

    /**
     * px转换dip
     */

    fun px2dip(px: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f).toInt()
    }

    fun px2sp(pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun sp2px(spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * 吐司 String
     *
     * @param msg
     */
    @JvmOverloads
    fun showToast(msg: String, longTimeType: Int = 0) {
        if (longTimeType == Toast.LENGTH_LONG) {
            Toast.makeText(context, msg, longTimeType).show()
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * 吐司 ResourcesId
     *
     * @param resId
     */
    @JvmOverloads
    fun showToast(resId: Int, longTimeType: Int = 0) {
        if (longTimeType == Toast.LENGTH_LONG) {
            Toast.makeText(context, getString(resId), longTimeType).show()
        } else {
            Toast.makeText(context, getString(resId), Toast.LENGTH_SHORT)
                    .show()
        }
    }

    /**
     * getResources
     *
     * @return Resources
     */
    val resources: Resources
        get() = context.resources

    /**
     * 通过资源id获取对应String数组
     *
     * @param id
     * @return
     */
    fun getStringArray(id: Int): Array<String> {
        return resources.getStringArray(id)
    }

    /**
     * 通过资源id获取对应Int数组
     *
     * @param id
     * @return
     */
    fun getIntegerArray(id: Int): IntArray {
        return resources.getIntArray(id)
    }

    /**
     * 通过资源id获取对应String
     *
     * @param id
     * @return
     */
    fun getString(id: Int): String {
        return resources.getString(id)
    }

    /**
     * 通过资源id获取对应颜色
     *
     * @param id
     * @return
     */
    fun getColor(id: Int): Int {
        return resources.getColor(id)
    }

    fun getDrawable(id: Int): Drawable {
        return resources.getDrawable(id)
    }

    /**
     * 获取View的缩略图
     *
     * @param view
     * @return ImageView
     */
    fun getDrawingCacheView(view: View): ImageView {
        view.destroyDrawingCache()
        view.isDrawingCacheEnabled = true
        val cache = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        val iv = ImageView(context)
        iv.setImageBitmap(cache)
        return iv
    }

    // hide nav bar
    fun hideNavbar(act: Activity) {
        if (Build.VERSION.SDK_INT >= 14) {
            act.window
                    .decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }


    /**
     * 创建Lable shape资源
     */
    fun createLableShape(color: Int): GradientDrawable {
        val drawableShape = GradientDrawable()
        drawableShape.setColor(color)
        drawableShape.cornerRadius = dip2px(5f).toFloat()
        drawableShape.setStroke(1, Color.TRANSPARENT)
        return drawableShape
    }

    fun crealeLableSelector(normalDrawable: Drawable, pressedDrawable: Drawable): StateListDrawable {
        val selector = StateListDrawable()
        selector.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
        selector.addState(intArrayOf(), normalDrawable)
        return selector
    }
}

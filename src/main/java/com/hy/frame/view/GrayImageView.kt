package com.hy.frame.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.AttributeSet

/**
 * 可变灰色图
 * author HeYan
 * time 2015/12/31 10:06
 */
class GrayImageView : android.support.v7.widget.AppCompatImageView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var isGray: Boolean = false

    override fun setImageBitmap(bm: Bitmap?) {
        if (bm == null) return
        if (isGray) {
            val width = bm.width
            val height = bm.height
            val grayBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            val c = Canvas(grayBmp)
            val paint = Paint()
            val cm = ColorMatrix()
            cm.setSaturation(0f)
            val f = ColorMatrixColorFilter(cm)
            paint.colorFilter = f
            c.drawBitmap(bm, 0f, 0f, paint)
            super.setImageBitmap(grayBmp)
        } else
            super.setImageBitmap(bm)
    }
}

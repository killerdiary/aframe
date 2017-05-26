package com.hy.frame.camera

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * ImageUtil

 * @author HeYan
 * *
 * @time 2017/4/28 15:36
 */
object ImageUtil {
    fun getRotateBitmap(bitmap: Bitmap, rotateDegree: Float): Bitmap {
        val matrix = Matrix()
        matrix.setRotate(rotateDegree)
        val rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        return rotateBitmap
    }
}

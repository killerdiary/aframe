package com.hy.frame.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

/**
 * Bitmap与DrawAble与byte[]与InputStream之间的转换工具类

 * @author HeYan
 * *
 * @time 2015-9-23 下午2:10:19
 */
class FormatTools {

    // 将byte[]转换成InputStream
    fun Byte2InputStream(b: ByteArray): InputStream {
        val bais = ByteArrayInputStream(b)
        return bais
    }

    // 将InputStream转换成byte[]
    fun InputStream2Bytes(`is`: InputStream): ByteArray? {
        var str = ""
        val readByte = ByteArray(1024)
        try {
            while (`is`.read(readByte, 0, 1024) != -1) {
                str += String(readByte).trim { it <= ' ' }
            }
            return str.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    // 将Bitmap转换成InputStream
    fun Bitmap2InputStream(bm: Bitmap): InputStream {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val `is` = ByteArrayInputStream(baos.toByteArray())
        return `is`
    }

    // 将Bitmap转换成InputStream
    fun Bitmap2InputStream(bm: Bitmap, quality: Int): InputStream {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos)
        val `is` = ByteArrayInputStream(baos.toByteArray())
        return `is`
    }

    // 将InputStream转换成Bitmap
    fun InputStream2Bitmap(`is`: InputStream): Bitmap {
        return BitmapFactory.decodeStream(`is`)
    }

    // Drawable转换成InputStream
    fun Drawable2InputStream(d: Drawable): InputStream {
        val bitmap = this.drawable2Bitmap(d)
        return this.Bitmap2InputStream(bitmap)
    }

    // InputStream转换成Drawable
    fun InputStream2Drawable(`is`: InputStream): Drawable {
        val bitmap = this.InputStream2Bitmap(`is`)
        return this.bitmap2Drawable(bitmap)
    }

    // Drawable转换成byte[]
    fun Drawable2Bytes(d: Drawable): ByteArray {
        val bitmap = this.drawable2Bitmap(d)
        return this.Bitmap2Bytes(bitmap)
    }

    // byte[]转换成Drawable
    fun Bytes2Drawable(b: ByteArray): Drawable {
        val bitmap = this.Bytes2Bitmap(b)
        return this.bitmap2Drawable(bitmap!!)
    }

    // Bitmap转换成byte[]
    fun Bitmap2Bytes(bm: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    // byte[]转换成Bitmap
    fun Bytes2Bitmap(b: ByteArray): Bitmap? {
        if (b.size != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.size)
        }
        return null
    }

    // Drawable转换成Bitmap
    fun drawable2Bitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    // Bitmap转换成Drawable
    fun bitmap2Drawable(bitmap: Bitmap): Drawable {
        val bd = BitmapDrawable(bitmap)
        return bd
    }

    companion object {
        private var tools: FormatTools? = FormatTools()

        val instance: FormatTools
            get() {
                if (tools == null) {
                    tools = FormatTools()
                    return tools!!
                }
                return tools!!
            }
    }
}
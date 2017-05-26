package com.hy.frame.camera

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * FileUtil

 * @author HeYan
 * *
 * @time 2017/4/28 15:33
 */
object FileUtil {
    private val TAG = "FileUtil"
    private val parentPath = Environment.getExternalStorageDirectory()
    private var storagePath = ""
    private val DST_FOLDER_NAME = "PlayCamera"

    private fun initPath(): String {
        if (storagePath == "") {
            storagePath = parentPath.absolutePath + "/" + DST_FOLDER_NAME
            val f = File(storagePath)
            if (!f.exists()) {
                f.mkdir()
            }
        }
        return storagePath
    }

    fun saveBitmap(b: Bitmap) {

        val path = initPath()
        val dataTake = System.currentTimeMillis()
        val jpegName = "$path/$dataTake.jpg"
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName)
        try {
            val fout = FileOutputStream(jpegName)
            val bos = BufferedOutputStream(fout)
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bos.flush()
            bos.close()
            Log.i(TAG, "saveBitmap success")
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            Log.i(TAG, "saveBitmap:fail")
            e.printStackTrace()
        }

    }

    val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == state) {
                return true
            }
            return false
        }
}

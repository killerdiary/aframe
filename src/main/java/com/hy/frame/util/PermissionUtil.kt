package com.hy.frame.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import java.util.ArrayList

/**
 * BS_Studio
 * @author HeYan
 * @time 2017/5/4 11:55
 */
object PermissionUtil {
    val REQUEST_PERMISSION_CAMERA = 201
    val REQUEST_PERMISSION_STORAGE = 202
    val REQUEST_PERMISSION_LOCATION = 203
    val REQUEST_PERMISSION_AUDIO = 204
    val REQUEST_PERMISSIONS_PHOTOGRAPH = 205
    val REQUEST_PERMISSIONS_AUDIO_RECORD = 206
    val REQUEST_PERMISSIONS_VIDEO_RECORD = 207
    //public static final String[] photographPermissions = new String[]{};

    fun requestCameraPermission(act: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            return true
        ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
        return false
    }

    fun requesStoragetPermission(act: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true
        ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_STORAGE)
        return false
    }

    fun requesLocationPermission(act: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true
        ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
        return false
    }

    fun requestPhotographPermission(act: Activity): Boolean {
        val permissions = ArrayList<String>()
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA)
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissions.size == 0) return true
        ActivityCompat.requestPermissions(act, permissions.toTypedArray(), REQUEST_PERMISSIONS_PHOTOGRAPH)
        return false
    }

    fun requestAudioRecordPermission(act: Activity): Boolean {
        val permissions = ArrayList<String>()
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.RECORD_AUDIO)
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissions.size == 0) return true
        ActivityCompat.requestPermissions(act, permissions.toTypedArray(), REQUEST_PERMISSIONS_AUDIO_RECORD)
        return false
    }

    fun requestVideoRecordPermission(act: Activity): Boolean {
        val permissions = ArrayList<String>()
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA)
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.RECORD_AUDIO)
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissions.size == 0) return true
        ActivityCompat.requestPermissions(act, permissions.toTypedArray(), REQUEST_PERMISSIONS_VIDEO_RECORD)
        return false
    }

    fun onRequestPermissionsResult(context: Context, requestCode: Int, permissions: Array<String>, grantResults: IntArray, listener: IRequestPermissionListener? = null) {
        var checkResult = false
        when (requestCode) {
            REQUEST_PERMISSION_CAMERA -> checkResult = checkSelfPermission(context, Manifest.permission.CAMERA)
            REQUEST_PERMISSION_STORAGE -> checkResult = checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            REQUEST_PERMISSION_LOCATION -> checkResult = checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            REQUEST_PERMISSIONS_PHOTOGRAPH -> checkResult = checkSelfPermission(context, Manifest.permission.CAMERA) && checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            REQUEST_PERMISSIONS_AUDIO_RECORD -> checkResult = checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) && checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            REQUEST_PERMISSIONS_VIDEO_RECORD -> checkResult = checkSelfPermission(context, Manifest.permission.CAMERA) && checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) && checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (listener != null) {
            if (checkResult)
                listener.onRequestPermissionSuccess(requestCode)
            else
                listener.onRequestPermissionFail(requestCode)
        }
    }

    fun checkSelfPermission(context: Context, permission: String): Boolean {
        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        MyToast.show(context, "您没有" + getPermissionFailMsg(permission) + "权限，请去权限管理中心开启")
        return false
    }

    fun getPermissionFailMsg(permission: String): String {
        when (permission) {
            Manifest.permission.CAMERA -> return "摄像头"
            Manifest.permission.RECORD_AUDIO -> return "录音"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> return "文件储存"
            Manifest.permission.ACCESS_FINE_LOCATION -> return "定位"
            else -> return "未知"
        }
    }

    interface IRequestPermissionListener {
        fun onRequestPermissionSuccess(requestCode: Int)

        fun onRequestPermissionFail(requestCode: Int)
    }
}
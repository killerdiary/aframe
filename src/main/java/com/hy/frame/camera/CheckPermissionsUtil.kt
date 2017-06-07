package com.hy.frame.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.util.Log

/**
 * CheckPermissionsUtil
 * @author HeYan
 * @time 2017/4/28 15:33
 */
class CheckPermissionsUtil(internal var mContext: Context) {

    private val needPermissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WAKE_LOCK, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS)

    private fun checkPermission(vararg needPermissions: String): Boolean {
        for (permission in needPermissions) {
            if (ActivityCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermission(activity: Activity, code: Int, vararg needPermissions: String) {
        ActivityCompat.requestPermissions(activity, needPermissions, code)
        Log.i(TAG, "request Permission...")
    }

    fun requestAllPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.i(TAG, "request All Permission...")
            for (permission in needPermissions) {
                if (!checkPermission(permission)) {
                    requestPermission(activity, 0, permission)
                }
            }
        }
    }

    companion object {
        val TAG = "CheckPermissionsUtil"
    }
}

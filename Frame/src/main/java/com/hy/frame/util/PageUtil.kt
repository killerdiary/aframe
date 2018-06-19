package com.hy.frame.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 *  页面跳转
 *  @author HeYan
 *  @time 2018/5/9 0009 上午 10:13
 */
object PageUtil {
    fun toWeb(context: Context, src: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(src)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toCall(context: Context, phone: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        if (phone.startsWith("tel:"))
            intent.data = Uri.parse(phone)
        else
            intent.data = Uri.parse("tel:$phone")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toVideo(context: Context, path: String, packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !(path.startsWith("http:") || path.startsWith("https:"))) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                val file = if (path.startsWith("file:")) File(Uri.parse(path).path) else File(path)
                val contentUri = FileProvider.getUriForFile(context, "$packageName.fileProvider", file)
                MyLog.d(contentUri.toString())
                intent.setDataAndType(contentUri, "video/*")
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
        } else {
            intent.setDataAndType(Uri.parse(path), "video/*")
        }
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toImage(context: Context, uri: Uri, packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !(uri.toString().startsWith("http:") || uri.toString().startsWith("https:"))) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(context, "$packageName.fileProvider", File(uri.path))
            intent.setDataAndType(contentUri, "image/*")
        } else {
            intent.setDataAndType(uri, "image/*")
        }
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 高德导航 http://lbs.amap.com/api/amap-mobile/guide/android/navigation
     * @param latitude 30.0f
     */
    fun toAmapNavi(context: Context, appName: String, latitude: String, longitude: String, destination: String): Boolean {
        val packageName = "com.autonavi.minimap"
        val exist = HyUtil.checkPackage(context, packageName)
        if (exist) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.`package` = packageName
            intent.data = Uri.parse("amapuri://route/plan/?sourceApplication=$appName&dname=$destination&dlat=$latitude&dlon=$longitude&dev=1&style=0")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(intent)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 百度导航 http://lbsyun.baidu.com/index.php?title=uri/api/android
     * @param latitude 30.0f
     */
    fun toBaiduNavi(context: Context, latitude: String, longitude: String, destination: String): Boolean {
        val packageName = "com.baidu.BaiduMap"
        val exist = HyUtil.checkPackage(context, packageName)
        if (exist) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.`package` = packageName
            intent.data = Uri.parse("baidumap://map/direction?destination=latlng:$latitude,$longitude|name:$destination&mode=driving")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(intent)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 腾讯导航 http://lbs.qq.com/uri_v1/index.html
     * @param latitude 30.0f
     */
    fun toTencentNavi(context: Context, appName: String, latitude: String, longitude: String, destination: String): Boolean {
        val packageName = "com.tencent.map"
        val exist = HyUtil.checkPackage(context, packageName)
        if (exist) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.`package` = packageName
            intent.data = Uri.parse("qqmap://map/routeplan?type=drive&to=$destination&tocoord=$latitude,$longitude&policy=0&referer=$appName")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(intent)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }
}
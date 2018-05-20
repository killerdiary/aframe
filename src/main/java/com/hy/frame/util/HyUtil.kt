package com.hy.frame.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.hy.frame.R
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 工具
 * @author HeYan
 * @time 2014年12月24日 下午1:43:34
 */
object HyUtil {


    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    fun <T : View> findViewById(id: Int, parent: View?): T? {
        return parent?.findViewById(id)
    }

    /**
     * 获取当前时间Date
     * @return 现在时间(Now)
     */
    val nowTime: String
        get() = getNowTime(null)

    fun getNowTime(type: String?): String {
        var ttype = type
        val d = Date(System.currentTimeMillis())
        // String type = "yyyy-MM-dd HH:mm:ss";
        if (ttype == null)
            ttype = "HH:mm:ss"
        val formatter = SimpleDateFormat(ttype, Locale.CHINA)
        return formatter.format(d)
    }

    /**
     * 获取当前时间Date
     */
    fun getDateTime(ltime: Long, type: String? = null): String {
        var time = ltime
        var ttype = type
        if ((time.toString() + "").length == 10) time *= 1000L
        if (ttype == null) ttype = "yyyy-MM-dd HH:mm:ss"
        val d = Date(time)
        val formatter = SimpleDateFormat(ttype, Locale.CHINA)
        return formatter.format(d)
    }

    fun stringToDateTime(strDate: String?, type: String? = null): Date? {
        var t = type
        if (strDate != null) {
            if (type == null) t = "yyyy-MM-dd HH:mm:ss"
            try {
                val sdf = SimpleDateFormat(t, Locale.CHINA)
                return sdf.parse(strDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

        }
        return null
    }

    fun getNowMinutes(): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        return hour * 60 + minute
    }

    fun getNowMinutes(str: String?): Int {
        if (str.isNullOrEmpty() || !str!!.contains(":")) return 0
        val strs = str!!.split(":")
        return strs[0].toInt() * 60 + strs[1].toInt()
    }

    /**
     * 获取控件的高度，如果获取的高度为0，则重新计算尺寸后再返回高度
     */
    fun getViewMeasuredHeight(view: View): Int {
        // int height = view.getMeasuredHeight();
        // if(0 < height){
        // return height;
        // }
        calcViewMeasure(view)
        return view.measuredHeight
    }

    /**
     * 获取控件的宽度，如果获取的宽度为0，则重新计算尺寸后再返回宽度
     */
    fun getViewMeasuredWidth(view: View): Int {
        // int width = view.getMeasuredWidth();
        // if(0 < width){
        // return width;
        // }
        calcViewMeasure(view)
        return view.measuredWidth
    }

    /**
     * 测量控件的尺寸
     */
    fun calcViewMeasure(view: View) {
        // int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        // int height = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        // view.measure(width,height);

        val width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        view.measure(width, expandSpec)
    }

    /**
     * 返回当前程序版本信息
     */
    fun getAppVersion(context: Context): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: Exception) {
            MyLog.e("VersionInfo|Exception:" + e)
            null
        }
    }

    /**
     * 检测该包名所对应的应用是否存在
     */
    fun checkPackage(context: Context, packageName: String): Boolean {
        if (TextUtils.isEmpty(packageName))
            return false
        return try {
            context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            true
        } catch (e: NameNotFoundException) {
            false
        }

    }

    private var lastTime: Long = 0

    /**
     * 是否是快速点击
     */
    val isFastClick: Boolean
        get() {
            val curTime = System.currentTimeMillis()
            if (curTime - lastTime < 500)
                return true
            lastTime = curTime
            return false
        }

    /**
     * 获取周几
     * @param week
     */
    fun getWeekName(week: Int, context: Context): String {
        when (week) {
            java.util.Calendar.SUNDAY -> context.getString(R.string.sunday)
            java.util.Calendar.MONDAY -> context.getString(R.string.monday)
            java.util.Calendar.TUESDAY -> context.getString(R.string.tuesday)
            java.util.Calendar.WEDNESDAY -> context.getString(R.string.wednesday)
            java.util.Calendar.THURSDAY -> context.getString(R.string.thursday)
            java.util.Calendar.FRIDAY -> context.getString(R.string.friday)
            java.util.Calendar.SATURDAY -> context.getString(R.string.saturday)
        }
        return context.getString(R.string.empty)
    }

    /**
     * @return null may be returned if the specified process not found
     */
    fun getProcessName(cxt: Context, pid: Int): String? {
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }

    private fun getCachePath(context: Context, dir: String?): String? {
        var f = context.getExternalFilesDir(dir)
        if (f == null) {
            f = context.filesDir
            if (!f.exists())
                f.mkdirs()
            if (dir.isNullOrEmpty()) return f.path
            val file = File(f, dir)
            // 判断文件夹存在与否，否则创建
            if (!file.exists() && file.mkdirs()) {
                return file.path
            }
            return f.path
        } else {
            return f.path
        }
    }

    /**
     * 获取相册缓存路径
     */
    fun getCachePathWeb(context: Context): String? {
        return getCachePath(context, "Web")
    }

    /**
     * 获取下载缓存路径
     */
    fun getCachePathDownload(context: Context): String? {
        return getCachePath(context, "Download")
    }

    /**
     * 获取相册缓存路径
     */
    fun getCachePathAlbum(context: Context): String? {
        return getCachePath(context, "Album")
    }

    /**
     * 获取剪切缓存路径
     */
    fun getCachePathCrop(context: Context): String? {
        return getCachePath(context, "CropFile")
    }

    /**
     * 获取音频缓存路径
     */
    fun getCachePathAudio(context: Context): String? {
        return getCachePath(context, "AudioFile")
    }

    /**
     * 获取音频缓存路径
     */
    fun getCachePathAudioC(context: Context): String? {
        return getCachePath(context, "AudioFileC")
    }

    /**
     * 获取视频缓存路径
     */
    fun getCachePathVideo(context: Context): String? {
        return getCachePath(context, "VideoFile")
    }

    /**
     * 获取视频缓存路径
     */
    fun getCachePathVideoC(context: Context): String? {
        return getCachePath(context, "VideoFileC")
    }

    /**
     * 获取图片缓存路径
     */
    fun getCachePathImage(context: Context): String? {
        return getCachePath(context, "Image")
    }

    /**
     * 获取缓存大小
     * @param context
     */
    fun getCacheSize(context: Context): String? {
        // 取得sdcard文件路径
        val path = getCachePath(context, null) ?: return "0M" // "mnt/sdcard"
        return FileUtil.getAutoFileOrFilesSize(path)
    }

    fun clearCache(context: Context) {
        FileUtil.delAllFile(getCachePath(context, null))
    }

    fun displayImage(requestManager: RequestManager?, imageView: ImageView?, url: String?) {
        display(requestManager, imageView, url, R.drawable.def_empty, R.drawable.def_empty)
    }

    fun displayHead(requestManager: RequestManager?, imageView: ImageView?, url: String?) {
        display(requestManager, imageView, url, R.drawable.def_empty, R.drawable.def_empty)
    }

    private fun display(requestManager: RequestManager?, imageView: ImageView?, url: String?, loading: Int, fail: Int, isGif: Boolean = false) {
        if (requestManager == null || imageView == null || url.isNullOrEmpty()) {
            imageView?.setImageResource(fail)
            return
        }
        MyLog.d("display", url!!)
        try {
            var builder = if (isGif) requestManager.asGif() else requestManager.asBitmap()
            builder = builder.load(url)
            val options = RequestOptions.noTransformation().placeholder(loading).error(fail)
            builder = builder.apply(options)
            builder.into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
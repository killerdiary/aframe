package com.hy.frame.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.hy.frame.R
import java.io.File
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * 工具
 * @author HeYan
 * @time 2014年12月24日 下午1:43:34
 */
object HyUtil {

    /**
     * 是否包含特殊符号
     */
    fun isContainSpecialSymbols(str: String?): Boolean {
        if (str == null) return false
        val regex = "[。，、：；？！‘’“”′.,﹑:;?!'\"〝〞＂+\\-*=<_~#\$&%‐﹡﹦﹤＿￣～﹟﹩﹠﹪@﹋﹉﹊｜‖^·¡…︴﹫﹏﹍﹎﹨\\\\ˇ¨¿—/（）〈〉‹›﹛﹜『』〖〗［］《》〔〕{}」【】︵︷︿︹﹁﹃︻︶︸﹀︺︾ˉ﹂﹄︼]"
        return !Pattern.compile(regex).matcher(str).matches()
    }

    /**
     * 是否是图片地址
     */
    fun isImagePath(str: String?): Boolean {
        if (str == null) return false
        return str.matches("(?i).+?\\.(png|jpg|gif|bmp)".toRegex())
    }

    /**
     * 手机号验证
     */
    fun isMobile(str: String?): Boolean {
        if (str == null) return false
        return str.matches("^[1][3-8][0-9]{9}$".toRegex())
    }

    /**
     * 是否是数字
     * @param str
     * @return 验证通过返回true
     */
    fun isNumber(str: String?): Boolean {
        if (str.isNullOrEmpty()) return false
        return Pattern.compile("[0-9]+").matcher(str).matches()
    }

    /**
     * 是否是英文
     */
    fun isEnglish(str: String?): Boolean {
        if (str == null)
            return false
        return Pattern.compile("[a-zA-Z]+").matcher(str).matches()
    }

    /**
     * 是否是中文
     * @param str
     * @return
     */
    fun isChinese(str: String?): Boolean {
        if (str == null)
            return false
        return Pattern.compile("^[\u4e00-\u9fa5]+$").matcher(str).matches()
    }

    /**
     * 是否是IP地址
     * @param str
     * @return
     */
    fun isIpAddress(str: String?): Boolean {
        if (str == null)
            return false
        return Pattern.compile("(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}").matcher(str).matches()
    }

    /**
     * 是否是身份证
     * @param str
     * @return
     */
    fun isIdentity(text: String): Boolean {
        var str = text
        if (isEmpty(str))
            return false
        if (str.length == 15)
            return Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$").matcher(str).matches()
        if (str.contains("x"))
            str = str.replace("x".toRegex(), "X")
        if (str.length == 18)
            return Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$").matcher(str).matches()
        return false
    }

    /**
     * 电话号码验证
     * @param str
     * @return 验证通过返回true
     */
    fun isPhone(text: String?): Boolean {
        var str: String? = text ?: return false
        val p1: Pattern?
        val p2: Pattern?
        val m: Matcher?
        var b = false
        if (str == null) return false
        str = str.replace("-".toRegex(), "")
        // p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$"); // 验证带区号的
        if (str.length == 11) {
            p1 = Pattern.compile("^[0][1-9]{2,3}[0-9]{5,10}$") // 验证带区号的
            m = p1!!.matcher(str)
            b = m!!.matches()
        } else if (str.length <= 9) {
            p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$") // 验证没有区号的
            m = p2!!.matcher(str)
            b = m!!.matches()
        }
        if (!b)
            return isMobile(str)
        return b
    }

    /**
     * 邮箱验证
     * @param str
     * @return 验证通过返回true
     */
    fun isEmail(str: String?): Boolean {
        if (str == null)
            return false
        return Pattern
                .compile("^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(]?)$")
                .matcher(str).matches()
    }

    /**
     * 是否是银行卡号
     */
    fun isBankCard(str: String): Boolean {
        if (isEmpty(str))
            return false
        val pattern = Pattern.compile("^(\\d{16}|\\d{19})$")
        return pattern.matcher(str).matches()
    }

    fun isNoEmpty(str: String?): Boolean {
        return !isEmpty(str)
    }

    fun isEmpty(str: String?): Boolean {
        if (null == str)
            return true
        if (str.isNullOrEmpty())
            return true
        if (str.trim { it <= ' ' }.isEmpty())
            return true
        if (str.indexOf("null") == 0)
            return true
        return false
    }

    fun isNoEmpty(datas: List<*>?): Boolean {
        return !isEmpty(datas)
    }

    fun isEmpty(datas: List<*>?): Boolean {
        if (datas == null)
            return true
        if (datas.isEmpty())
            return true
        return false
    }

    /**
     * 去掉多余的0
     */
    fun removeNumberZero(text: String): String {
        var str = text
        if (isEmpty(str)) {
            return "0"
        }
        if (str.indexOf(".") > 0) {
            str = str.replace("0+?$".toRegex(), "")// 去掉多余的0
            str = str.replace("[.]$".toRegex(), "")// 如最后一位是.则去掉
        }
        return str
    }

    /**
     * 转换成Money格式
     */
    fun formatToMoney(obj: Any): String {
        return DecimalFormat("0.00").format(obj)
    }

    /**
     * 把字体结果dimen转化成原sp值
     */
    fun floatToSpDimension(value: Float, context: Context): Float {
        return value / context.resources.displayMetrics.scaledDensity
    }

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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
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
     * 是否是车牌号
     */
    fun isCarNumber(str: String): Boolean {
        if (isNoEmpty(str))
            return Pattern.compile("^[\u4e00-\u9fa5|A-Z][A-Z][A-Z_0-9]{5}$").matcher(str).matches()
        return false
    }

    val weeks = arrayOf<String>()

    /**
     * 获取周几
     * @param week
     */
    fun getWeekName(week: Int): String? {
        when (week) {
            Calendar.SUNDAY -> return "周日"
            Calendar.MONDAY -> return "周一"
            Calendar.TUESDAY -> return "周二"
            Calendar.WEDNESDAY -> return "周三"
            Calendar.THURSDAY -> return "周四"
            Calendar.FRIDAY -> return "周五"
            Calendar.SATURDAY -> return "周六"
        }
        return null
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

    fun getStringFromJson(json: JsonObject?, key: String): String? {
        if (json == null || !json.has(key) || json.get(key).isJsonNull) return null
        return json.get(key).asString
    }

    fun getIntFromJson(json: JsonObject?, key: String): Int {
        if (json == null || !json.has(key) || json.get(key).isJsonNull) return 0
        return json.get(key).asInt
    }

    fun getBooleanFromJson(json: JsonObject?, key: String): Boolean {
        if (json == null || !json.has(key) || json.get(key).isJsonNull) return false
        return json.get(key).asBoolean
    }

    fun <T> getListFromJson(json: JsonObject?, key: String, cls: Class<T>): MutableList<T>? {
        return getListFromJson(json?.get(key), cls)
    }

    fun <T> getListFromJson(data: JsonElement?, cls: Class<T>): MutableList<T>? {
        if (data == null || !data.isJsonArray) return null
        return try {
            val beans: Array<T> = Gson().fromJson(data, TypeToken.getArray(cls).type)
            val items = ArrayList(Arrays.asList(*beans))
            items
        } catch (e: Exception) {
            null
        }
    }

    fun <T> getObjectFromJson(data: JsonElement?, cls: Class<T>): T? {
        if (data == null || !data.isJsonObject) return null
        return try {
            Gson().fromJson(data, cls)
        } catch (e: Exception) {
            null
        }
    }
}
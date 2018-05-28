package com.hy.app.ui.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.safe.JsCallback
import android.webkit.safe.LogUtils
import com.amap.api.location.CoordinateConverter
import com.amap.api.location.DPoint
import com.hy.app.BuildConfig
import com.hy.app.R
import com.hy.app.common.BaseActivity
import com.hy.app.ui.camera.MultImageActivity
import com.hy.app.ui.dialog.ConfirmDialog
import com.hy.app.util.LocationUtil
import com.hy.app.util.WebCacheShare
import com.hy.app.widget.InnerWebView
import com.hy.frame.app.BaseDialog
import com.hy.frame.app.IBaseFragment
import com.hy.frame.bean.MyHandler
import com.hy.frame.ui.LoadingDialog
import com.hy.frame.util.DimensionUtil
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.frame.util.MyToast
import org.json.JSONException
import org.json.JSONObject


/**
 * JS和原生交互
 * @author HeYan
 * @time 2017/9/12 17:44
 */
class NativeJsActivity : BaseActivity() {
    private var webView: InnerWebView? = null

    override fun getLayoutId(): Int = R.layout.act_web_nativejs

    override fun initView() {
        webView = findViewById(R.id.web_nativejs_webView)
    }

    private var handler: MyHandler? = null
        get() {
            if (field == null)
                field = MyHandler(this, object : MyHandler.HandlerListener {
                    override fun handleMessage(msg: Message) {

                    }
                })
            return field
        }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun initData() {
        initHeaderBack(R.string.webjs_test)
        val settings = webView!!.settings


        if (LogUtils.isDebug()) {
            webView?.trySetWebDebuggEnabled()
        }
        webView?.fixedAccessibilityInjectorException()
        settings.javaScriptEnabled = true
        webView?.addJavascriptInterface(MyJavascriptInterface(this, object : IMyJsListener {
            override fun showLoading(message: String, cancelable: Boolean) {
                showLoadingDlg(message, cancelable)
            }

            override fun closeLoading() {
                closeLoadingDlg()
            }

            override fun reloadWebPage() {

            }

            override fun openRefresh() {

            }

            override fun closeRefresh() {

            }

            override fun endRefresh() {

            }

            override fun isScrollBottom(): Boolean {
                return false
            }

            override fun jsDriveScroll(scrollY: Double) {

            }


        }), "andJs")



        webView!!.loadUrl(HTML)
    }

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {
        when (v.id) {

        }
    }

    private var loadingDialog: LoadingDialog? = null

    fun showLoadingDlg(message: String?, cancelable: Boolean = true) {
        if (loadingDialog == null)
            loadingDialog = LoadingDialog(getCurContext())
        loadingDialog?.setCancelable(cancelable)
        if (message != null)
            loadingDialog?.updateMsg(message)
        loadingDialog?.show()
    }

    fun closeLoadingDlg() {
        if (loadingDialog != null) loadingDialog!!.dismiss()
    }

    open class MyJavascriptInterface {
        //val FLAG_WINDOW_CLOSE = 990
        private val fragment: IBaseFragment?
        private val act: BaseActivity
        private val listener: IMyJsListener?

        constructor (act: BaseActivity, listener: IMyJsListener) {
            this.fragment = null
            this.act = act
            this.listener = listener
        }

        constructor (fragment: IBaseFragment, listener: IMyJsListener) {
            this.fragment = fragment
            this.act = fragment.getFragment().activity as BaseActivity
            this.listener = listener
        }

        private fun getContext(): Context {
            return act
        }

        /**
         * 获取设备信息
         * appVersion:	app版本号
         * sysName:系统类型
         * sysVersion:	系统版本号
         * deviceType: 	手机机型
         * @return JSON
         */
        @JavascriptInterface
        fun getDeviceInfo(): JSONObject {
            val json = JSONObject()
            try {
                json.put("versionName", BuildConfig.VERSION_NAME)
                json.put("versionCode", BuildConfig.VERSION_CODE)
                json.put("deviceName", Build.MODEL)
                json.put("deviceVersion", Build.VERSION.SDK_INT)
                json.put("deviceType", "Android")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return json
        }

        /**
         * cityName:
         * cityCode:
         * address:
         * lat:
         * lng:
         *
         * @return JSON
         */
        @JavascriptInterface
        fun getLocationInfo(): JSONObject? {
            //        cityName 	成都市
            //        cityCode 	510100
            //        adress 	人民南路三段汇日央扩国际广场
            //        lat 	30.6460613969
            //        lng 	104.0659125889
            val util = LocationUtil.getInstance(getContext(), null)
            val lastLocation = util.lastLocation
            if (lastLocation != null) {
                val json = JSONObject()
                try {
                    json.put("cityName", lastLocation.city)
                    json.put("cityCode", lastLocation.cityCode)
                    json.put("adress", lastLocation.address)
                    json.put("lat", lastLocation.latitude)
                    json.put("lng", lastLocation.longitude)
                    return json
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                util.startLocation(null, act)
            }
            return null
        }

        @JavascriptInterface
        fun getStatusBarHeight(): Int {
            return DimensionUtil.px2dip(act.getStatusBarHeight().toFloat(), getContext()).toInt()
        }

        @JavascriptInterface
        fun getNavigationBarHeight(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                (DimensionUtil.px2dip( getContext().resources.getDimensionPixelSize(R.dimen.header_height).toFloat(),getContext()) + getStatusBarHeight()).toInt()
            } else DimensionUtil.px2dip(  getContext().getResources().getDimensionPixelSize(R.dimen.header_height).toFloat(),getContext()).toInt()
        }

        /**
         * 显示定位坐标
         *
         * @param title     标题
         * @param latitude  纬度
         * @param longitude 经度
         */
        @JavascriptInterface
        fun showLocation(title: String, latitude: String, longitude: String) {
            //if (listener != null) listener.showLocation(title, latitude, longitude);
//            try {
//                val bundle = Bundle()
//                bundle.putString(Constant.FLAG, title)
//                bundle.putParcelable(Constant.FLAG2, LatLng(java.lang.Double.parseDouble(latitude), java.lang.Double.parseDouble(longitude)))
//                startAct(LocationActivity::class.java, bundle)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

        }

        /**
         * 显示导航
         *
         * @param title     标题
         * @param latitude  纬度
         * @param longitude 经度
         */
        @SuppressLint("WrongConstant")
        @JavascriptInterface
        fun openNavi(title: String, latitude: String, longitude: String) {
            try {
                //            cat=android.intent.category.DEFAULT
                //            dat=androidamap://navi?sourceApplication=appname&poiname=fangheng&lat=36.547901&lon=104.258354&dev=1&style=2
                //            pkg=com.autonavi.minimap
                val amap = HyUtil.checkPackage(getContext(), "com.autonavi.minimap")
                if (amap) {
                    val intent = Intent.parseUri("androidamap://navi?sourceApplication=" + getContext().getString(R.string.app_name) + "&poiname=" + title + "&lat=" + latitude + "&lon=" + longitude + "&dev=1&style=2", Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.`package` = "com.autonavi.minimap"
                    getContext().startActivity(intent)
                    return
                }
                val baidu = HyUtil.checkPackage(getContext(), "com.baidu.BaiduMap")
                if (baidu) {
                    val converter = CoordinateConverter(getContext())
                    // CoordType.GPS 待转换坐标类型
                    converter.from(CoordinateConverter.CoordType.BAIDU)
                    // sourceLatLng待转换坐标点 DPoint类型
                    converter.coord(DPoint(java.lang.Double.parseDouble(latitude), java.lang.Double.parseDouble(longitude)))
                    // 执行转换操作
                    val desLatLng = converter.convert()
                    //Intent intent = Intent.parseUri("androidamap://path?sourceApplication=GasStation&sid=BGVIS1&slat="+latitude+"&slon="+longitude+"&sname="+title+"&did=BGVIS2&dlat=36.3&dlon=116.2&dname=终点位置&dev=1&m=2&t=0", Intent.FLAG_ACTIVITY_NEW_TASK);
                    //Intent intent = Intent.parseUri("intent://map/direction?origin=latlng:" + latitude + "," + longitude + "|name:" + title + "&destination="+title+"&mode=driving®ion=成都&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", Intent.FLAG_ACTIVITY_NEW_TASK);
                    val intent = Intent.parseUri("intent://map/direction?destination=latlng:" + desLatLng.getLatitude() + "," + desLatLng.getLongitude() + "|name:" + title + "&mode=driving#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", Intent.FLAG_ACTIVITY_NEW_TASK)
                    getContext().startActivity(intent)
                    return
                }
//                val bundle = Bundle()
//                bundle.putString(Constant.FLAG, title)
//                bundle.putParcelable(Constant.FLAG2, LatLng(java.lang.Double.parseDouble(latitude), java.lang.Double.parseDouble(longitude)))
//                startAct(NaviActivity::class.java, bundle)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**
         * 提示-自动消失
         *
         * @param message 要提示的内容
         */
        @JavascriptInterface
        fun alert(message: String) {
            MyToast.show(getContext(), message)
        }

        /**
         * 确定对话框-有确定和取消
         *
         * @param json {
         * title: 标题
         * message: 内容
         * confirm: 确定按钮的文本
         * cancel: 取消按钮的文本
         * }
         */
        @JavascriptInterface
        fun confirm(json: JSONObject) {
            confirm(json, null, null)
        }

        /**
         * 确定对话框-有确定和取消
         *
         * @param json            {
         * title: 标题
         * message: 内容
         * confirm: 确定按钮的文本
         * cancel: 取消按钮的文本
         * }
         * @param confirmCallback 点击确定后的回调
         */
        @JavascriptInterface
        fun confirm(json: JSONObject, confirmCallback: JsCallback) {
            confirm(json, confirmCallback, null)
        }

        /**
         * 确定对话框-有确定和取消
         *
         * @param json            {
         * title: 标题
         * message: 内容
         * confirm: 确定按钮的文本
         * cancel: 取消按钮的文本
         * hideCancel: 是否隐藏取消按钮
         * }
         * @param confirmCallback 点击确定后的回调
         * @param cancelCallback  点击取消后的回调
         */
        @JavascriptInterface
        fun confirm(json: JSONObject, confirmCallback: JsCallback?, cancelCallback: JsCallback?) {
            try {
                val title = if (json.has("title")) json.getString("title") else "title"
                val message = if (json.has("message")) json.getString("message") else "message"
                val confirm = if (json.has("confirm")) json.getString("confirm") else getContext().getString(R.string.confirm)
                val cancel = if (json.has("cancel")) json.getString("cancel") else getContext().getString(R.string.cancel)
                val hideCancel = json.has("hideCancel") && json.getBoolean("hideCancel")
                confirm(title, message, confirm, cancel, hideCancel, confirmCallback, cancelCallback)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        /**
         * 确定对话框(有确定和取消按钮)
         *
         * @param title           标题
         * @param message         要提示的内容
         * @param confirm         确定按钮的文本
         * @param cancel          取消按钮的文本
         * @param hideCancel      是否隐藏取消按钮
         * @param confirmCallback 确定回调
         * @param cancelCallback  取消回调
         */
        private fun confirm(title: String, message: String, confirm: String, cancel: String, hideCancel: Boolean, confirmCallback: JsCallback?, cancelCallback: JsCallback?) {
            val dlg = ConfirmDialog(getContext(), title, message, confirm, cancel, hideCancel)
            if (confirmCallback != null)
                dlg.listener = object : BaseDialog.IConfirmListener {
                    override fun onDlgConfirm(dialog: BaseDialog) {
                        try {
                            confirmCallback.apply("confirm")
                        } catch (je: JsCallback.JsCallbackException) {
                            je.printStackTrace()
                        }
                    }
                }
            if (cancelCallback != null)
                dlg.setOnCancelListener({
                    try {
                        cancelCallback.apply("cancel")
                    } catch (je: JsCallback.JsCallbackException) {
                        je.printStackTrace()
                    }
                })
            dlg.show()
        }

//        /**
//         * 显示加载提示框
//         */
//        @JavascriptInterface
//        fun showLoading() {
//            showLoading(getContext().getString(R.string.empty))
//        }
//
        /**
         * 显示加载提示框
         *
         * @param message 提示语
         */
        @JavascriptInterface
        fun showLoading(message: String) {
            showLoading(message, true)
        }

        @JavascriptInterface
        fun showLoading(message: String, cancelable: Boolean) {
            MyLog.e(javaClass, "showLoading")
            listener?.showLoading(message, cancelable)
        }

        /**
         * 关闭加载提示框
         */
        @JavascriptInterface
        fun closeLoading() {
            MyLog.e(javaClass, "closeLoading")
            listener?.closeLoading()
        }
//
//        /**
//         * 打开网页提示
//         *
//         * @param json {
//         * width: 宽度比例 0—1
//         * height: 高度比例 0—1
//         * }
//         */
//        @JavascriptInterface
//        fun showLayer(json: JSONObject) {
//            showLayer(json, null)
//        }
//
//        /**
//         * 打开网页提示
//         *
//         * @param json            {
//         * width: 宽度比例 0—1
//         * height: 高度比例 0—1
//         * }
//         * @param confirmCallback 点击确定后的回调
//         */
//        @JavascriptInterface
//        fun showLayer(json: JSONObject, confirmCallback: JsCallback?) {
//            try {
//                val width = java.lang.Float.valueOf(json.getString("width"))!!
//                val height = java.lang.Float.valueOf(json.getString("height"))!!
//                val path = json.getString("path")
//                val dlg = WebDialog(getContext(), width, height, path)
//                if (confirmCallback != null)
//                    dlg.setListener(object : BaseDialog.IConfirmListener {
//                        override fun onDlgConfirm(dlg: BaseDialog) {
//                            try {
//                                confirmCallback!!.apply("confirm")
//                            } catch (je: JsCallback.JsCallbackException) {
//                                je.printStackTrace()
//                            }
//
//                        }
//                    })
//                dlg.show()
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//        }
//
//
//        /**
//         * 打开连接-新窗口
//         *
//         * @param json {
//         * type:’1(打开连接)’||2’打开原生’,
//         * value:’如果type:1,value为连接,如果type为2: value为object’,
//         * requestCode: 请求码
//         * }
//         */
//        @JavascriptInterface
//        fun pushUrl(json: JSONObject) {
//            try {
//                if (!json.has("type") || !json.has("value")) return
//                val type = json.getInt("type")
//                var requestCode = 0
//                if (json.has("requestCode"))
//                    requestCode = json.getInt("requestCode")
//                if (type == 1) {
//                    val url = json.getString("value")
//                    if (url.startsWith(MyWebView.SCHEME_IM) || url.startsWith(MyWebView.SCHEME_IM_HIDE)) {
//                        val strs = url.split("//".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//                        if (strs.size < 2) return
//                        val path = "http://" + strs[1]
//                        val bundle = Bundle()
//                        bundle.putString(Constant.FLAG_TITLE, "加载中...")
//                        bundle.putString(Constant.FLAG, path)
//                        bundle.putBoolean(WebActivity.TAG_HEADER_HIDE, url.startsWith(MyWebView.SCHEME_IM_HIDE))
//                        startAct(WebActivity::class.java, bundle, requestCode)
//                    } else if (url.startsWith("http://") || url.startsWith("https://")) {
//                        val bundle = Bundle()
//                        bundle.putString(Constant.FLAG_TITLE, "加载中...")
//                        bundle.putString(Constant.FLAG, url)
//                        bundle.putBoolean(WebActivity.TAG_HEADER_HIDE, url.startsWith(MyWebView.SCHEME_IM_HIDE))
//                        startAct(WebActivity::class.java, bundle, requestCode)
//                    }
//                } else if (type == 2) {
//                    val obj = json.getJSONObject("value")
//                    val destination = obj.getString("destination")
//                    if (TextUtils.equals(destination, "SuperPartnerApply")) {
//                        startAct(SuperPartnerActivity::class.java, null)
//                    } else if (TextUtils.equals(destination, "WithdrawAssets")) {
//                        startAct(WithdrawActivity::class.java, null, requestCode)
//                    }
//                }
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//        }


        @JavascriptInterface
        fun closeWindow() {
            act.finish()
        }

        //
//
//        @JavascriptInterface
//        fun jumpLogin() {
//            startAct(LoginActivity::class.java, null)
//        }
//
        @JavascriptInterface
        fun setCache(key: String, value: String) {
            WebCacheShare[getContext()].putString(key, value)
        }

        @JavascriptInterface
        fun getCache(key: String): String? {
            return WebCacheShare[getContext()].getString(key)
        }

        @JavascriptInterface
        fun removeCache(key: String) {
            WebCacheShare[getContext()].remove(key)
        }
//
//        /**
//         * 下拉刷新-开启
//         */
//        @JavascriptInterface
//        fun openRefresh() {
//            if (listener != null)
//                listener!!.openRefresh()
//        }
//
//        /**
//         * 下拉刷新-禁止
//         */
//        @JavascriptInterface
//        fun closeRefresh() {
//            if (listener != null)
//                listener!!.closeRefresh()
//        }
//
//        /**
//         * 下拉刷新-关闭
//         */
//        @JavascriptInterface
//        fun endRefresh() {
//            if (listener != null)
//                listener!!.endRefresh()
//        }
//
//        /**
//         * 是否滚动到底部
//         */
//        @JavascriptInterface
//        fun isScrollBottom(): Boolean {
//            if (listener != null)
//                listener!!.isScrollBottom()
//            return false
//        }
//
//        /**
//         * 分享页面
//         *
//         * @param json {
//         * title: 标题
//         * message: 内容
//         * url: 链接
//         * thumb: 图片链接
//         * ...
//         * }
//         */
//        @JavascriptInterface
//        fun openShare(json: JSONObject) {
//            val shareInfo = ShareInfo()
//            try {
//                if (json.has("title")) {
//                    shareInfo.setTitle(json.getString("title"))
//                }
//                if (json.has("message")) {
//                    shareInfo.setMessage(json.getString("message"))
//                }
//                if (json.has("url")) {
//                    shareInfo.setUrl(json.getString("url"))
//                }
//                if (json.has("thumb")) {
//                    shareInfo.setThumb(json.getString("thumb"))
//                }
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//            AllShareDialog(getContext() as Activity, shareInfo).show()
//        }

        /**
         * 打开图片浏览页面
         *
         * @param json {
         * items: 图片数组
         * position: 选择的图片在数据中的索引
         * }
         */
        @JavascriptInterface
        fun showPictures(json: JSONObject) {
            try {
                val array = json.getJSONArray("items")
                val datas = ArrayList<String>()
                val size = array.length()
                (0 until size).mapTo(datas) { array[it].toString() }
                var position = 0
                if (json.has("position"))
                    position = json.getInt("position")
                startAct(MultImageActivity::class.java, MultImageActivity.newArguments(datas, position))
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
//
//        /**
//         * 支付宝支付
//         *
//         * @param json
//         */
//        @JavascriptInterface
//        fun toAlipay(json: JSONObject, successCallback: JsCallback?, failCallback: JsCallback?) {
//            if (act == null) {
//                showInfoMessage("当前页面不支持支付")
//                return
//            }
//            try {
//                val util = PayUtil(act, PayUtil.PayStyle.Alipay)
//                util.setListener(object : PayUtil.PayListener {
//                    override fun paySuccess(style: PayUtil.PayStyle) {
//                        if (successCallback != null)
//                            try {
//                                successCallback!!.apply()
//                            } catch (e: JsCallback.JsCallbackException) {
//                                e.printStackTrace()
//                            }
//
//                    }
//
//                    override fun payFail(msg: String) {
//                        if (failCallback != null)
//                            try {
//                                failCallback!!.apply(msg)
//                            } catch (e: JsCallback.JsCallbackException) {
//                                e.printStackTrace()
//                            }
//
//                    }
//                })
//                val title = json.getString("title")
//                val desc = json.getString("desc")
//                val order_no = json.getString("order_no")
//                val money = json.getString("money")
//                val callback = json.getString("callback")
//                val rsa = json.getString("rsa")
//                util.toAlipay(order_no, title, desc, money, callback, rsa)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//        }
//
//        /**
//         * 微信支付
//         *
//         * @param json{ partnerid;// 1353241802,
//         * prepayid;// wx20160616110932ce39cb8f000485105933,
//         * noncestr;// B9y3FsJzDQK4dStM,
//         * timestamp;// 1466046572
//         * sign;
//         * }
//         */
//        @JavascriptInterface
//        fun toWXPay(json: JSONObject, successCallback: JsCallback?, failCallback: JsCallback?) {
//            if (act == null) {
//                showInfoMessage("当前页面不支持支付")
//                return
//            }
//            try {
//                val util = PayUtil(act, PayUtil.PayStyle.WeixinPay)
//                util.setListener(object : PayUtil.PayListener {
//                    override fun paySuccess(style: PayUtil.PayStyle) {
//                        if (successCallback != null)
//                            try {
//                                successCallback!!.apply()
//                            } catch (e: JsCallback.JsCallbackException) {
//                                e.printStackTrace()
//                            }
//
//                    }
//
//                    override fun payFail(msg: String) {
//                        if (failCallback != null)
//                            try {
//                                failCallback!!.apply(msg)
//                            } catch (e: JsCallback.JsCallbackException) {
//                                e.printStackTrace()
//                            }
//
//                    }
//                })
//                (act as? WebActivity)?.initPayReceiver(object : PayUtil.PayListener {
//                    override fun paySuccess(style: PayUtil.PayStyle) {
//                        if (successCallback != null)
//                            try {
//                                successCallback!!.apply()
//                            } catch (e: JsCallback.JsCallbackException) {
//                                e.printStackTrace()
//                            }
//
//                    }
//
//                    override fun payFail(msg: String) {
//                        if (failCallback != null)
//                            try {
//                                failCallback!!.apply(msg)
//                            } catch (e: JsCallback.JsCallbackException) {
//                                e.printStackTrace()
//                            }
//
//                    }
//                })
//                val info = Gson().fromJson<Any>(json.toString(), WxPayInfo::class.java)
//                util.toWxPay(info)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }
//
//        /**
//         * 获取头信息
//         */
//        @JavascriptInterface
//        fun getHeaderStr(): String {
//            return HeaderUtils.getInstance(getContext()).getHeaderStr()
//        }
//
//        /**
//         * 刷新界面
//         */
//        @JavascriptInterface
//        fun reloadWebPage() {
//            if (listener != null) {
//                listener!!.reloadWebPage()
//            }
//        }
//
//        @JavascriptInterface
//        fun jsDriveScroll(scrollY: Double) {
//            if (listener != null) {
//                listener!!.jsDriveScroll(scrollY)
//            }
//        }
        /**
         * 启动Activity
         */
        private fun startAct(cls: Class<*>, bundle: Bundle? = null, intent: Intent? = null) {
            var i = intent
            if (i == null)
                i = Intent()
            if (bundle != null)
                i.putExtra(BUNDLE, bundle)
            i.putExtra(LAST_ACT, this.javaClass.simpleName)
            i.setClass(getContext(), cls)
            getContext().startActivity(i)
        }
    }

    interface IMyJsListener {
        fun showLoading(message: String, cancelable: Boolean)

        fun closeLoading()
        /**
         * 加载
         */
        fun reloadWebPage()

        /**
         * 下拉刷新-开启
         */
        fun openRefresh()

        /**
         * 下拉刷新-禁止
         */
        fun closeRefresh()

        /**
         * 下拉刷新-关闭
         */
        fun endRefresh()

        /**
         * 是否滚动到底部
         */
        fun isScrollBottom(): Boolean

        /**
         * 滚动
         * @param scrollY Y滚动距离
         */
        fun jsDriveScroll(scrollY: Double)
    }

    companion object {
        val HTML = "file:///android_asset/nativejs.html"
    }
}

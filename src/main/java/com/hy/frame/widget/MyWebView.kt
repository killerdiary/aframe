package com.hy.frame.widget

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ZoomButtonsController
import com.hy.frame.util.MyLog

/**
 * @author HeYan
 *
 * @title
 *
 * @time 2015/11/23 18:35
 */
class MyWebView  constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : WebView(context, attrs, defStyleAttr) {

    init {
        init()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        val wbs = settings
        wbs.javaScriptEnabled = true
        wbs.javaScriptCanOpenWindowsAutomatically = true
        wbs.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        wbs.setAppCacheEnabled(true)
        wbs.blockNetworkImage = false
        wbs.databaseEnabled = true
        wbs.saveFormData = false
        wbs.allowFileAccess = true
        wbs.domStorageEnabled = true
        wbs.setGeolocationEnabled(true)
        // 设置可以支持缩放
        wbs.setSupportZoom(true)
        // 设置出现缩放工具
        wbs.builtInZoomControls = true
        // 扩大比例的缩放
        wbs.useWideViewPort = true
        // 自适应屏幕
        wbs.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        wbs.loadWithOverviewMode = true
        hideZoom()
        closeSoftWare()
        isVerticalFadingEdgeEnabled = true
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        setWebViewClient(object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // If dialing phone (tel:5551212)
                if (url.startsWith(WebView.SCHEME_TEL)) {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse(url)
                        context.startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException) {
                        MyLog.e("Error dialing " + url + ": " + e.toString())
                    }

                } else if (url.startsWith("geo:")) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        context.startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException) {
                        MyLog.e("Error showing map " + url + ": " + e.toString())
                    }

                } else if (url.startsWith(WebView.SCHEME_MAILTO)) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        context.startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException) {
                        MyLog.e("Error sending email " + url + ": " + e.toString())
                    }

                } else if (url.startsWith("sms:")) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        // Get address
                        var address: String? = null
                        val parmIndex = url.indexOf('?')
                        if (parmIndex == -1) {
                            address = url.substring(4)
                        } else {
                            address = url.substring(4, parmIndex)
                            // If body, then set sms body
                            val uri = Uri.parse(url)
                            val query = uri.query
                            if (query != null) {
                                if (query.startsWith("body=")) {
                                    intent.putExtra("sms_body", query.substring(5))
                                }
                            }
                        }
                        intent.data = Uri.parse("sms:" + address)
                        intent.putExtra("address", address)
                        intent.type = "vnd.android-dir/mms-sms"
                        context.startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException) {
                        MyLog.e("Error sending sms " + url + ":" + e.toString())
                    }

                } else if (url.startsWith("market:")) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        context.startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException) {
                        MyLog.e("Error loading Google Play Store: " + url + " " + e.toString())
                    }

                } else if (url.endsWith(".apk")) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        context.startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException) {
                        MyLog.e("Error dialing " + url + ": " + e.toString())
                    }

                } else {
                    MyLog.d("url-----> " + url)
                    view.loadUrl(url)
                }// All else
                // If dialing phone (tel:5551212)
                // Android Market
                // If sms:5551212?body=This is the message
                // If sending email (mailto:abc@corp.com)
                // If displaying map (geo:0,0?q=address)
                return true
            }

        })
    }

    @SuppressLint("NewApi")
    private fun hideZoom() {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            settings.displayZoomControls = false
        } else {
            setZoomControlHide(this)
        }
    }

    internal var zoomController: ZoomButtonsController? = null

    fun setZoomControlHide(view: View) {
        try {
            val webview = Class.forName("android.webkit.WebView")
            val method = webview.getMethod("getZoomButtonsController")
            zoomController = method.invoke(this, *arrayOf<Any>()) as ZoomButtonsController
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun closeSoftWare() {
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)
        }
    }
}

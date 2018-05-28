package com.hy.app.ui.web

import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.View


import com.hy.app.R
import com.hy.app.common.BaseActivity
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * com.hy.app.ui.web
 * author HeYan
 * time 2016/8/26 16:54
 */
class WebX5Activity : BaseActivity() {
    private var webView: WebView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFormat(PixelFormat.TRANSLUCENT)
    }
    override fun getLayoutId(): Int {
        return R.layout.act_web_x5
    }

    override fun initView() {
        webView = findViewById(R.id.web_x5_webView)
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun initData() {
        initHeaderBack(R.string.web_x5)
        webView!!.settings.javaScriptEnabled = true
        webView!!.addJavascriptInterface(MyJavascriptInterface(getCurContext()), "appJs")
        webView!!.webChromeClient = object : WebChromeClient() {

        }
        webView!!.webViewClient = object :WebViewClient(){

        }
        //webView!!.loadUrl("file:///android_asset/test.html")
        webView!!.loadUrl("http://jupai.ivears.cn/#/courseDetail/9")
    }

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {}


}

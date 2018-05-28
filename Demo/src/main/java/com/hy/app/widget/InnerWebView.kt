package com.hy.app.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.webkit.JsPromptResult
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.safe.LogUtils
import android.webkit.safe.SafeWebView
import com.hy.app.ui.web.UnsafeWebActivity

@SuppressLint("SetJavaScriptEnabled")
class InnerWebView : SafeWebView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    init {
        if (LogUtils.isDebug()) {
            trySetWebDebuggEnabled()
        }
        fixedAccessibilityInjectorException()
        val ws = settings
        ws.javaScriptEnabled = true
        webChromeClient = InnerWebChromeClient()
        webViewClient = InnerWebViewClient()
    }

    override fun addJavascriptInterface(interfaceObj: Any?, interfaceName: String?) {
        super.addJavascriptInterface(interfaceObj, interfaceName)
    }

    inner class InnerWebChromeClient : SafeWebView.SafeWebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress) // 务必放在方法体的第一行执行；
            // to do your work
            // ...
        }

        override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {
            // to do your work
            // ...
            return super.onJsPrompt(view, url, message, defaultValue, result) // 务必放在方法体的最后一行执行，或者用if判断也行；
        }

        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        }

    }

    inner class InnerWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url == UnsafeWebActivity.HTML) {
                view.context.startActivity(Intent(view.context, UnsafeWebActivity::class.java))
                return true
            }
            return super.shouldOverrideUrlLoading(view, url)
        }


    }
}
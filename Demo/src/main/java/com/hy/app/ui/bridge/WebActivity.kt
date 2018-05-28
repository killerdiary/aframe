package com.hy.app.ui.bridge

import android.view.View
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebSettings
import android.webkit.WebView

import com.hy.app.common.BaseActivity

class WebActivity : BaseActivity() {


    override fun getLayoutId(): Int {
        return 0
    }

    override fun initView() {
        val wv = WebView(this)
        setContentView(wv)
        val ws = wv.settings
        ws.javaScriptEnabled = true
        wv.webChromeClient = CustomChromeClient("HostApp", HostJsScope::class.java)
        wv.loadUrl("file:///android_asset/test.html")
    }

    override fun initData() {}

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {}

    inner class CustomChromeClient(injectedName: String, injectedCls: Class<*>) : InjectedChromeClient(injectedName, injectedCls) {

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            // to do your work
            // ...
            return super.onJsAlert(view, url, message, result)
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            // to do your work
            // ...
        }

        override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {
            // to do your work
            // ...
            return super.onJsPrompt(view, url, message, defaultValue, result)
        }
    }
}

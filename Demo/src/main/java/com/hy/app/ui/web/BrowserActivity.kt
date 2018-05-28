package com.hy.app.ui.web

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.webkit.*
import com.hy.app.R
import com.hy.app.common.BaseActivity
import com.hy.frame.util.MyLog

class BrowserActivity : BaseActivity() {

    private var webView: WebView? = null

    override fun isSingleLayout(): Boolean = true

    override fun getLayoutId(): Int = R.layout.act_web_browser

    override fun initView() {
        webView = findViewById(R.id.web_browser_webView)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initData() {
        initHeaderBack(R.string.web_broswer, R.mipmap.ic_refresh)
        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                MyLog.d(javaClass, "shouldInterceptRequest " + url)
                return super.shouldInterceptRequest(view, url)
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    MyLog.d(javaClass, "shouldInterceptRequest " + request?.url)
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                MyLog.d(javaClass, "shouldOverrideUrlLoading " + url)
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    MyLog.d(javaClass, "shouldOverrideUrlLoading " + request?.url)
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

            }
        }
        webView?.webChromeClient = object :WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

        }
        showLoading()
        webView?.loadUrl(HTML)
    }

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {}

    companion object {
        val HTML = "http://jupai.ivears.cn/#/classfiy?type=3"
    }
}
package com.hy.frame.widget


import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hy.frame.util.MyLog


/**
 * 浏览代理
 * author HeYan
 * time 2016/1/15 16:52
 */
open class MyWebViewClient : WebViewClient() {
    var isLoadError: Boolean = false


    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val context = view.context
        Log.d(MyLog.TAG, "shouldOverrideUrlLoading-----> " + url)
        // If dialing phone (tel:5551212)
        if (url.startsWith(WebView.SCHEME_TEL)) {
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                Log.d(MyLog.TAG, "Error dialing " + url + ": " + e.toString())
            }

        } else if (url.startsWith("geo:")) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                Log.d(MyLog.TAG, "Error showing map " + url + ": " + e.toString())
            }

        } else if (url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                Log.d(MyLog.TAG, "Error sending email " + url + ": " + e.toString())
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
                Log.d(MyLog.TAG, "Error sending sms " + url + ":" + e.toString())
            }

        } else if (url.startsWith("market:")) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                Log.d(MyLog.TAG, "Error loading Google Play Store: " + url + " " + e.toString())
            }

        } else if (url.endsWith(".apk")) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            } catch (e: android.content.ActivityNotFoundException) {
                Log.d(MyLog.TAG, "Error dialing " + url + ": " + e.toString())
            }

        } else {
            Log.d(MyLog.TAG, "url-----> " + url)
            view.loadUrl(url)
        }// All else
        // If dialing phone (tel:5551212)
        // Android Market
        // If sms:5551212?body=This is the message
        // If sending email (mailto:abc@corp.com)
        // If displaying map (geo:0,0?q=address)
        return true
    }
    //    @Override
    //    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
    //
    //    }

    open override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {}
}

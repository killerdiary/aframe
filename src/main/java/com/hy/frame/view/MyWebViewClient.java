package com.hy.frame.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hy.frame.util.MyLog;

/**
 * 浏览代理
 * author HeYan
 * time 2016/1/15 16:52
 */
public class MyWebViewClient extends WebViewClient {
    protected boolean isLoadError;


    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Context context = view.getContext();
        Log.d(MyLog.TAG, "shouldOverrideUrlLoading-----> " + url);
        // If dialing phone (tel:5551212)
        if (url.startsWith(WebView.SCHEME_TEL)) {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Log.d(MyLog.TAG, "Error dialing " + url + ": " + e.toString());
            }
        }
        // If displaying map (geo:0,0?q=address)
        else if (url.startsWith("geo:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Log.d(MyLog.TAG, "Error showing map " + url + ": " + e.toString());
            }
        }
        // If sending email (mailto:abc@corp.com)
        else if (url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Log.d(MyLog.TAG, "Error sending email " + url + ": " + e.toString());
            }
        }
        // If sms:5551212?body=This is the message
        else if (url.startsWith("sms:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                // Get address
                String address = null;
                int parmIndex = url.indexOf('?');
                if (parmIndex == -1) {
                    address = url.substring(4);
                } else {
                    address = url.substring(4, parmIndex);
                    // If body, then set sms body
                    Uri uri = Uri.parse(url);
                    String query = uri.getQuery();
                    if (query != null) {
                        if (query.startsWith("body=")) {
                            intent.putExtra("sms_body", query.substring(5));
                        }
                    }
                }
                intent.setData(Uri.parse("sms:" + address));
                intent.putExtra("address", address);
                intent.setType("vnd.android-dir/mms-sms");
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Log.d(MyLog.TAG, "Error sending sms " + url + ":" + e.toString());
            }
        }
        // Android Market
        else if (url.startsWith("market:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Log.d(MyLog.TAG, "Error loading Google Play Store: " + url + " " + e.toString());
            }
        }
        // If dialing phone (tel:5551212)
        else if (url.endsWith(".apk")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Log.d(MyLog.TAG, "Error dialing " + url + ": " + e.toString());
            }
        }
        // All else
        else {
            Log.d(MyLog.TAG, "url-----> " + url);
            view.loadUrl(url);
        }
        return true;
    }
//    @Override
//    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//
//    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

    }
}

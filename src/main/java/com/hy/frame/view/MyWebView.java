package com.hy.frame.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

import com.hy.frame.util.MyLog;

import java.lang.reflect.Method;

/**
 * @author HeYan
 * @title
 * @time 2015/11/23 18:35
 */
public class MyWebView extends WebView {
    public MyWebView(Context context) {
        this(context,null);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        WebSettings wbs = getSettings();
        wbs.setJavaScriptEnabled(true);
        wbs.setJavaScriptCanOpenWindowsAutomatically(true);
        wbs.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wbs.setAppCacheEnabled(true);
        wbs.setBlockNetworkImage(false);
        wbs.setDatabaseEnabled(true);
        wbs.setSaveFormData(false);
        wbs.setAllowFileAccess(true);
        wbs.setDomStorageEnabled(true);
        wbs.setGeolocationEnabled(true);
        // 设置可以支持缩放
        wbs.setSupportZoom(true);
        // 设置出现缩放工具
        wbs.setBuiltInZoomControls(true);
        // 扩大比例的缩放
        wbs.setUseWideViewPort(true);
        // 自适应屏幕
        wbs.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wbs.setLoadWithOverviewMode(true);
        hideZoom();
        closeSoftWare();
        setVerticalFadingEdgeEnabled(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // If dialing phone (tel:5551212)
                if (url.startsWith(WebView.SCHEME_TEL)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(url));
                         getContext().startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        MyLog.e("Error dialing " + url + ": " + e.toString());
                    }
                }
                // If displaying map (geo:0,0?q=address)
                else if (url.startsWith("geo:")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        getContext().startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        MyLog.e("Error showing map " + url + ": " + e.toString());
                    }
                }
                // If sending email (mailto:abc@corp.com)
                else if (url.startsWith(WebView.SCHEME_MAILTO)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        getContext().startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        MyLog.e("Error sending email " + url + ": " + e.toString());
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
                        getContext().startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        MyLog.e("Error sending sms " + url + ":" + e.toString());
                    }
                }
                // Android Market
                else if (url.startsWith("market:")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        getContext().startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        MyLog.e("Error loading Google Play Store: " + url + " " + e.toString());
                    }
                }
                // If dialing phone (tel:5551212)
                else if (url.endsWith(".apk")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        getContext().startActivity(intent);
                    } catch (android.content.ActivityNotFoundException e) {
                        MyLog.e("Error dialing " + url + ": " + e.toString());
                    }
                }
                // All else
                else {
                    MyLog.d("url-----> " + url);
                    view.loadUrl(url);
                }
                return true;
            }

        });
    }

    @SuppressLint("NewApi")
    private void hideZoom() {
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getSettings().setDisplayZoomControls(false);
        } else {
            setZoomControlHide(this);
        }
    }

    ZoomButtonsController zoomController;

    public void setZoomControlHide(View view) {
        try {
            Class webview = Class.forName("android.webkit.WebView");
            Method method = webview.getMethod("getZoomButtonsController");
            zoomController = (ZoomButtonsController) method.invoke(this, new Object[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void closeSoftWare() {
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
    }
}

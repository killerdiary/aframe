package com.hy.app.ui.web;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.hy.frame.util.MyToast;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;

public class MyJavascriptInterface {
    private Context context;


    public MyJavascriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void alert(String msg) {
        MyToast.INSTANCE.show(context, msg);
    }

    @JavascriptInterface
    public void confirm(String msg, JsPromptResult callback) {
        MyToast.INSTANCE.show(context, msg);
        if (callback != null) {
            alert("sssssssdfsdafsadfsdf");
            callback.confirm("sb");
        }
    }
}
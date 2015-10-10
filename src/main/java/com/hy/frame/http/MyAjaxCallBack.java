package com.hy.frame.http;

import net.tsz.afinal.http.AjaxCallBack;

public abstract class MyAjaxCallBack extends AjaxCallBack<String> {
    private int requestCode;

    public MyAjaxCallBack(int requestCode) {
        super();
        this.requestCode = requestCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

}

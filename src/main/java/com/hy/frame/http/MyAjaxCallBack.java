package com.hy.frame.http;

import com.hy.frame.bean.ResultInfo;

import net.tsz.afinal.http.AjaxCallBack;

public abstract class MyAjaxCallBack extends AjaxCallBack<String> {

    private ResultInfo result;

    public MyAjaxCallBack(ResultInfo result) {
        super();
        this.result = result;
    }

    public ResultInfo getResult() {
        return result;
    }
}

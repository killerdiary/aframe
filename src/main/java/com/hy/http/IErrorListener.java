package com.hy.http;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hy.frame.bean.ResultInfo;

/**
 * Error Listener
 * author HeYan
 * time 2016/4/13 15:04
 */
public abstract class IErrorListener implements Response.ErrorListener {
    private ResultInfo result;

    public ResultInfo getResult() {
        return result;
    }

    public void setResult(ResultInfo result) {
        this.result = result;
    }
}

package com.hy.http;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.Map;

public class StringRequest extends com.android.volley.toolbox.StringRequest {
    private final AjaxParams mParams;

    public StringRequest(String url, AjaxParams params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(Method.POST, url, params, listener, errorListener);
    }

    public StringRequest(int method, String url, AjaxParams params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        mParams = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mParams != null)
            return mParams.getUrlParams();
        return null;
    }
}

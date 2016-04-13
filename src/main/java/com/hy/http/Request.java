package com.hy.http;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.hy.frame.bean.ResultInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Request
 * author HeYan
 * time 2016/3/26 17:07
 */
public abstract class Request<T> extends com.android.volley.Request<T> {
    private Map<String, String> headerParams;
    private Object mPostBody;
    private HttpEntity httpEntity;
    private ResultInfo result;

    public Request(int method, String url, Object params, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mPostBody = params;
        if (this.mPostBody != null && this.mPostBody instanceof AjaxParams) {// contains file
            this.httpEntity = ((AjaxParams) this.mPostBody).getEntity();
        }
    }

    public void addHeader(String key, String value) {
        if (headerParams == null)
            headerParams = new ConcurrentHashMap<String, String>();
        else
            headerParams.remove(key);
        headerParams.put(key, value);
    }

    public void setHeader(Map<String, String> headerParams) {
        this.headerParams = headerParams;
    }

    /**
     * mPostBody is null or Map<String, String>, then execute this method
     */
    @SuppressWarnings("unchecked")
    protected Map<String, String> getParams() throws AuthFailureError {
        if (this.httpEntity == null && this.mPostBody != null && this.mPostBody instanceof Map<?, ?>) {
            return ((Map<String, String>) this.mPostBody);//common Map<String, String>
        }
        return null;//process as json, xml or MultipartRequestParams
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headerParams != null)
            return headerParams;
        return super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        if (httpEntity != null) {
            return httpEntity.getContentType();
        }
        return super.getBodyContentType();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (this.mPostBody != null && this.mPostBody instanceof String) {//process as json or xml
            String postString = (String) mPostBody;
            if (postString.length() != 0) {
                try {
                    return postString.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                return null;
            }
        }
        if (this.httpEntity != null) {//process as MultipartRequestParams
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                httpEntity.writeTo(baos);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return baos.toByteArray();
        }
        return super.getBody();// mPostBody is null or Map<String, String>
    }

    public ResultInfo getResult() {
        return result;
    }

    public void setResult(ResultInfo result) {
        this.result = result;
    }
}
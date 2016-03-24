package com.hy.http;

import android.util.LruCache;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.hy.frame.util.MyLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件上传
 */
public class MultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();
    private final Response.Listener<String> mListener;
    private final AjaxParams mParams;


    public MultipartRequest(String url, AjaxParams params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mParams = params;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        if (mParams == null) return;
        if (mParams.getFileParams() != null && mParams.getFileParams().size() > 0) {
            int currentIndex = 0;
            int size = mParams.getFileParams().size();
            int lastIndex = size - 1;
            for (ConcurrentHashMap.Entry<String, AjaxParams.FileWrapper> entry : mParams.getFileParams().entrySet()) {
                AjaxParams.FileWrapper file = entry.getValue();
                if (file.inputStream != null) {
                    boolean isLast = currentIndex == lastIndex;
                    if (file.contentType != null) {
                        entity.addPart(entry.getKey(), file.getFileName(), file.inputStream, file.contentType, isLast);
                    } else {
                        entity.addPart(entry.getKey(), file.getFileName(), file.inputStream, isLast);
                    }
                }
                currentIndex++;
            }
            long l = entity.getContentLength();
            MyLog.i(size + "个，长度：" + l);
        }
        if (mParams.getUrlParams() != null && mParams.getUrlParams().size() > 0) {
            for (Map.Entry<String, String> entry : mParams.getUrlParams().entrySet()) {
                entity.addPart(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        MyLog.i("parseNetworkResponse");
        if (VolleyLog.DEBUG) {
            if (response.headers != null) {
                for (Map.Entry<String, String> entry : response.headers
                        .entrySet()) {
                    VolleyLog.d(entry.getKey() + "=" + entry.getValue());
                }
            }
        }
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }


    /*
     * (non-Javadoc)
     *
     * @see com.android.volley.Request#getHeaders()
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        VolleyLog.d("getHeaders");
        Map<String, String> headers = super.getHeaders();
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        return headers;
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
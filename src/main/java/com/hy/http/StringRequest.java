package com.hy.http;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * A canned request for getting an File at a given URL
 */
public class StringRequest extends Request<String> {
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    private final ISuccessListener mListener;


    public StringRequest(String url, AjaxParams params, ISuccessListener listener, IErrorListener errorListener) {
        this(Method.GET, url, params, listener, errorListener);
    }

    public StringRequest(int method, String url, AjaxParams params, ISuccessListener listener, IErrorListener errorListener) {
        super(Method.GET, url, params, errorListener);
        mListener = listener;
        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, HttpHeaderParser.parseCharset(response.headers, DEFAULT_PARAMS_ENCODING)), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(String data) {
        if (mListener != null) {
            mListener.onSuccess(data);
        }
    }

    public class FileProgress {
        private long fileSize;
        private long downloadSize;
        private boolean cancel;

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public long getDownloadSize() {
            return downloadSize;
        }

        public void setDownloadSize(long downloadSize) {
            this.downloadSize = downloadSize;
        }

        public boolean isCancel() {
            return cancel;
        }

        public void setCancel(boolean cancel) {
            this.cancel = cancel;
        }
    }
}

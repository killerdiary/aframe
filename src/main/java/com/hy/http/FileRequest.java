package com.hy.http;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.hy.frame.util.MyLog;

import net.tsz.afinal.http.entityhandler.EntityCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * A canned request for getting an File at a given URL
 */
public class FileRequest extends com.android.volley.Request<File> {

    /**
     * Socket timeout in milliseconds for image requests
     */
    private static final int FILE_TIMEOUT_MS = 1000;

    /**
     * Default number of retries for image requests
     */
    private static final int FILE_MAX_RETRIES = 2;
    /**
     * Default backoff multiplier for image requests
     */
    private static final float FILE_BACKOFF_MULT = 2f;
    private final AjaxParams mParams;
    private final Response.Listener<File> mListener;
    private boolean mStop;
    private String target;
    private boolean isResume;

    public boolean isStop() {
        return mStop;
    }

    public void setStop(boolean stop) {
        this.mStop = stop;
    }

    /**
     * Decoding lock so that we don't decode more than one image at a time (to avoid OOM's)
     */
    private static final Object sDecodeLock = new Object();
    private long curFileLength;

    public FileRequest(String url, AjaxParams params, String target, boolean isResume, Response.Listener<File> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mParams = params;
        mListener = listener;
        this.target = target;
        this.isResume = isResume;
        setRetryPolicy(new DefaultRetryPolicy(FILE_TIMEOUT_MS, FILE_MAX_RETRIES, FILE_BACKOFF_MULT));
        if (isResume && target != null) {
            File downloadFile = new File(target);
            long fileLen = 0;
            if (downloadFile.isFile() && downloadFile.exists()) {
                fileLen = downloadFile.length();
            }
            curFileLength = fileLen;
            //request.setHeader("RANGE", "bytes=" + fileLen + "-");
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    protected Response<File> parseNetworkResponse(NetworkResponse response) {
        // Serialize all decode on a global lock to reduce concurrent heap usage.
        synchronized (sDecodeLock) {
            try {
                return doParse(response);
            } catch (OutOfMemoryError e) {
                VolleyLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                return Response.error(new ParseError(e));
            }
        }
    }

    /**
     * The real guts of parseNetworkResponse. Broken out for readability.
     */
    private Response<File> doParse(NetworkResponse response) {
        MyLog.d(getClass(), "" + response.data.length);
        byte[] data = response.data;
        File targetFile = null;
        if (!TextUtils.isEmpty(target) && target.trim().length() > 0)
            return Response.error(new VolleyError("target is Empty!"));
        targetFile = new File(target);
        try {
            if (!targetFile.exists())
                targetFile.createNewFile();
            if (mStop) {
                return Response.error(new VolleyError("user stop download thread!"));
            }
            long current = 0;
            FileOutputStream os = null;
            if (isResume) {
                current = targetFile.length();
                os = new FileOutputStream(target, true);
            } else {
                os = new FileOutputStream(target);
            }
//            InputStream input = new FileInputStream();
            long count = data.length + current;
//            if (current >= count || mStop) {
//                return targetFile;
//            }
//            int readLen = 0;
//            byte[] buffer = new byte[1024];
//            while (!mStop && !(current >= count) && ((readLen = input.read(buffer, 0, 1024)) > 0)) {//未全部读取
//                os.write(buffer, 0, readLen);
//                current += readLen;
//                callback.callBack(count, current, false);
//            }
//            callback.callBack(count, current, true);
            os.write(data);
            if (mStop && current < count) { //用户主动停止
                throw new IOException("user stop download thread");
            }
        } catch (IOException e) {
            return Response.error(new VolleyError("file create fail!", e.getCause()));
        }
        if (targetFile == null) {
            return Response.error(new ParseError(response));
        } else {
            return Response.success(targetFile, HttpHeaderParser.parseCacheHeaders(response));
        }
    }

    @Override
    protected void deliverResponse(File response) {
        mListener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mParams != null)
            return mParams.getUrlParams();
        return null;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }
}

package com.hy.http;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

/**
 * A canned request for getting an File at a given URL
 */
public class FileRequest extends Request<FileRequest.FileProgress> {

    /**
     * Socket timeout in milliseconds for file requests
     */
    private static final int FILE_TIMEOUT_MS = 2500;

    /**
     * Default number of retries for file requests
     */
    private static final int FILE_MAX_RETRIES = 200;
    /**
     * Default backoff multiplier for file requests
     */
    private static final float FILE_BACKOFF_MULT = 2f;

    private final Response.Listener<FileProgress> mListener;
    private boolean mStop;
    private String target;
    private boolean isResume;
    private File file;
    private File tempFile;
    private boolean cancel;

    /**
     * Mark this request as canceled.  No callback will be delivered.
     */
    public void cancel() {
        cancel = true;
    }

    /**
     * Returns true if this request has been canceled.
     */
    public boolean isCanceled() {
        return cancel;
    }

    /**
     * Decoding lock so that we don't decode more than one image at a time (to avoid OOM's)
     */
    private static final Object sDecodeLock = new Object();
    private long curFileLength;
    private FileProgress fileProgress;

    public FileRequest(String url, String target, AjaxParams params, boolean isResume, Response.Listener<FileProgress> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, params, errorListener);
        file = new File(target);
        tempFile = new File(target + ".tmp");
        mListener = listener;
        this.target = target;
        this.isResume = isResume;
        fileProgress = new FileProgress();
        setRetryPolicy(new DefaultRetryPolicy(FILE_TIMEOUT_MS, FILE_MAX_RETRIES, FILE_BACKOFF_MULT));
        if (isResume) {
            long fileLen = 0;
            if (tempFile.isFile() && tempFile.exists()) {
                fileLen = tempFile.length();
            }
            curFileLength = fileLen;
            addHeader("Range", "bytes=" + fileLen + "-");
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    @Override
    protected Response<FileProgress> parseNetworkResponse(NetworkResponse response) {
        if (!isCanceled()) {
            if (tempFile.canRead() && tempFile.length() > 0) {
                if (tempFile.renameTo(file)) {
                    return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                } else {
                    return Response.error(new VolleyError("Can't rename the download temporary file!"));
                }
            } else {
                return Response.error(new VolleyError("Download temporary file was invalid!"));
            }
        }
        return Response.error(new VolleyError("Request was Canceled!"));
    }

    @Override
    protected void deliverResponse(FileProgress progress) {
        if (mListener != null) {
            mListener.onResponse(progress);
        }
    }

    public String getHeader(HttpResponse response, String key) {
        Header header = response.getFirstHeader(key);
        return header == null ? null : header.getValue();
    }

    public boolean isSupportRange(HttpResponse response) {
        if (TextUtils.equals(getHeader(response, "Accept-Ranges"), "bytes")) {
            return true;
        }
        String value = getHeader(response, "Content-Range");
        return value != null && value.startsWith("bytes");
    }

    public boolean isGzipContent(HttpResponse response) {
        return TextUtils.equals(getHeader(response, "Content-Encoding"), "gzip");
    }

    public byte[] entityToBytes(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        long fileSize = entity.getContentLength();
        if (fileSize <= 0) {
            VolleyLog.d("Response doesn't present Content-Length!");
        }
        long downloadedSize = tempFile.length();
        boolean isSupportRange = isSupportRange(response);
        if (isSupportRange) {
            fileSize += downloadedSize;
            // Verify the Content-Range Header, to ensure temporary file is part of the whole file.
            // Sometime, temporary file length add response content-length might greater than actual file length,
            // in this situation, we consider the temporary file is invalid, then throw an exception.
            String realRangeValue = getHeader(response, "Content-Range");
            // response Content-Range may be null when "Range=bytes=0-"
            if (!TextUtils.isEmpty(realRangeValue)) {
                String assumeRangeValue = "bytes " + downloadedSize + "-" + (fileSize - 1);
                if (TextUtils.indexOf(realRangeValue, assumeRangeValue) == -1) {
                    throw new IllegalStateException(
                            "The Content-Range Header is invalid Assume[" + assumeRangeValue + "] vs Real[" + realRangeValue + "], " +
                                    "please remove the temporary file [" + tempFile + "].");
                }
            }
        }
        // Compare the store file size(after download successes have) to server-side Content-Length.
        // temporary file will rename to store file after download success, so we compare the
        // Content-Length to ensure this request already download or not.
        if (fileSize > 0 && file.length() == fileSize) {
            // Rename the store file to temporary file, mock the download success. ^_^
            file.renameTo(tempFile);
            // Deliver download progress.
            fileProgress.setFileSize(fileSize);
            fileProgress.setDownloadSize(fileSize);
            deliverResponse(fileProgress);
            return null;
        }
        RandomAccessFile tmpFileRaf = null;
        try {
            tmpFileRaf = new RandomAccessFile(tempFile, "rw");
            // If server-side support range download, we seek to last point of the temporary file.
            if (isSupportRange) {
                tmpFileRaf.seek(downloadedSize);
            } else {
                // If not, truncate the temporary file then start download from beginning.
                tmpFileRaf.setLength(0);
                downloadedSize = 0;
            }
            InputStream in = entity.getContent();
            // Determine the response gzip encoding, support for HttpClientStack download.
            if (isGzipContent(response) && !(in instanceof GZIPInputStream)) {
                in = new GZIPInputStream(in);
            }
            byte[] buffer = new byte[6 * 1024]; // 6K buffer
            int offset;
            while ((offset = in.read(buffer)) != -1) {
                tmpFileRaf.write(buffer, 0, offset);
                downloadedSize += offset;
                fileProgress.setFileSize(fileSize);
                fileProgress.setDownloadSize(downloadedSize);
                deliverResponse(fileProgress);
                if (isCanceled()) {
                    fileProgress.setCancel(true);
                    deliverResponse(fileProgress);
                    break;
                }
            }
            tmpFileRaf.close();
            // Close the InputStream and release the resources by "consuming the content".
            entity.consumeContent();
        } catch (IOException e) {
            e.printStackTrace();
            VolleyLog.v("Error occured when calling consumingContent");
        }
        return new byte[0];
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
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

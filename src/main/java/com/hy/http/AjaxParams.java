package com.hy.http;


import com.hy.http.file.Binary;
import com.hy.http.file.FileBinary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AjaxParams for key/value (include Multipart file)
 *
 * @author panxw
 */
public class AjaxParams {
    private static final String ENCODING = "UTF-8";
    private long qid;
    private ConcurrentHashMap<String, String> urlParams;
    private ConcurrentHashMap<String, Binary> fileParams;
    private PostData postData;

    public AjaxParams() {
        init();
    }

    public AjaxParams(String key, String value) {
        init();
        put(key, value);
    }

    private void init() {
        urlParams = new ConcurrentHashMap<>();
        fileParams = new ConcurrentHashMap<>();
    }

    /**
     * Add {@link Object} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public void put(String key, Object value) {
        if (key != null && value != null) {
            put(key, String.valueOf(value));
        }
    }

    /**
     * Add {@link String} param.
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    /**
     * Add {@link Integer} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public void put(String key, int value) {
        put(key, String.valueOf(value));
    }

    /**
     * Add {@link Long} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public void put(String key, long value) {
        put(key, String.valueOf(value));
    }

    /**
     * Add {@link Double} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public void put(String key, double value) {
        put(key, String.valueOf(value));
    }

    /**
     * Add {@link Float} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public void put(String key, float value) {
        put(key, String.valueOf(value));
    }

    /**
     * Add {@link Boolean} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public void put(String key, boolean value) {
        put(key, String.valueOf(value));
    }

    /**
     * @param key
     * @param file
     */
    @Deprecated
    public void put(String key, FileBinary file) {
        fileParams.put(key, file);
    }

    public void put(PostData postData) {
        this.postData = postData;
    }

    public ConcurrentHashMap<String, String> getUrlParams() {
        return urlParams;
    }

    public long getQid() {
        return qid;
    }

    public void setQid(long qid) {
        this.qid = qid;
    }

    public ConcurrentHashMap<String, Binary> getFileParams() {
        return fileParams;
    }

    public PostData getPostData() {
        return postData;
    }

    public void setFileParams(ConcurrentHashMap<String, Binary> fileParams) {
        this.fileParams = fileParams;
    }

    public String getUrlParamsString() {
        StringBuilder sb = new StringBuilder();
        if (urlParams != null && urlParams.size() > 0) {
            int size = urlParams.size();
            int i = 0;
            for (Map.Entry<String, String> map : urlParams.entrySet()) {
                i++;
                sb.append(map.getKey());
                sb.append("=");
                sb.append(map.getValue());
                if (i < size)
                    sb.append("&");
            }
        }
        return sb.toString();
    }

    public String getFileParamsString() {
        StringBuilder sb = new StringBuilder();
        if (fileParams != null && fileParams.size() > 0) {
            int size = fileParams.size();
            int i = 0;
            for (Map.Entry<String, Binary> map : fileParams.entrySet()) {
                i++;
                sb.append(map.getKey());
                sb.append("=");
                sb.append(map.getValue());
                if (i < size)
                    sb.append("&");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getUrlParamsString());
        String fileParamsString = getFileParamsString();
        if (sb.length() > 0 && fileParamsString != null && fileParamsString.length() > 0) {
            sb.append("&");
            sb.append(fileParamsString);
        }
        return sb.toString();
    }
}
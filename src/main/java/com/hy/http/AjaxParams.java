package com.hy.http;


import com.hy.http.file.Binary;
import com.hy.http.file.FileBinary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AjaxParams for key/value (include Multipart file)
 *
 * @author HeYan
 * @time 2017/5/8 15:20
 */
public class AjaxParams {
    //private static final String ENCODING = "UTF-8";
    private long qid;
    private Map<String, String> urlParams;
    private Map<String, Binary> fileParams;
    private PostData postData;

    public AjaxParams() {
    }

    public AjaxParams(String key, String value) {
        put(key, value);
    }


    /**
     * Add {@link Object} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    @Deprecated
    public void put(String key, Object value) {
        if (key != null && value != null) {
            put(key, String.valueOf(value));
        }
    }

    /**
     * Add {@link String} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, String value) {
        if (key != null && value != null) {
            if (urlParams == null) urlParams = new ConcurrentHashMap<>();
            urlParams.put(key, value);
        }
        return this;
    }

    /**
     * Add {@link Integer} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, int value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * Add {@link Long} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, long value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * Add {@link Double} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, double value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * Add {@link Float} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, float value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * Add {@link Boolean} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, boolean value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * Add {@link FileBinary} param.
     *
     * @param key  param name.
     * @param file param value.
     */
    public AjaxParams put(String key, FileBinary file) {
        if (fileParams == null)
            fileParams = new ConcurrentHashMap<>();
        fileParams.put(key, file);
        return this;
    }

    public AjaxParams setQid(long qid) {
        this.qid = qid;
        return this;
    }

    public long getQid() {
        return qid;
    }

    public AjaxParams setPostData(PostData postData) {
        this.postData = postData;
        return this;
    }

    public PostData getPostData() {
        return postData;
    }

    public Map<String, String> getUrlParams() {
        return urlParams;
    }


    public Map<String, Binary> getFileParams() {
        return fileParams;
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
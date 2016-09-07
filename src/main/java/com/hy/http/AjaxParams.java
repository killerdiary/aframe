package com.hy.http;

import com.yolanda.nohttp.Binary;
import com.yolanda.nohttp.FileBinary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AjaxParams for key/value (include Multipart file)
 *
 * @author panxw
 */
public class AjaxParams {
    private static String ENCODING = "UTF-8";
    protected ConcurrentHashMap<String, String> urlParams;
    protected ConcurrentHashMap<String, Binary> fileParams;
    private PostData postData;

    public AjaxParams() {
        init();
    }

    public AjaxParams(String key, String value) {
        init();
        put(key, value);
    }

    private void init() {
        urlParams = new ConcurrentHashMap<String, String>();
        fileParams = new ConcurrentHashMap<String, Binary>();
    }

    /**
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
        put(key, value + "");
    }

    /**
     * Add {@link Long} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public void put(String key, long value) {
        put(key, value + "");
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

    public ConcurrentHashMap<String, Binary> getFileParams() {
        return fileParams;
    }

    public PostData getPostData() {
        return postData;
    }

    public void setFileParams(ConcurrentHashMap<String, Binary> fileParams) {
        this.fileParams = fileParams;
    }

    @Deprecated
    private static class FileWrapper {
        public InputStream inputStream;
        public String fileName;
        public String contentType;

        public FileWrapper(InputStream inputStream, String fileName,
                           String contentType) {
            this.inputStream = inputStream;
            this.fileName = fileName;
            this.contentType = contentType;
        }

        public String getFileName() {
            if (fileName != null) {
                return fileName;
            } else {
                return "nofilename";
            }
        }
    }
}
package com.hy.http;

import com.hy.frame.util.Charsets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AjaxParams {
    protected ConcurrentHashMap<String, String> urlParams;
    protected ConcurrentHashMap<String, FileWrapper> fileParams;

    public AjaxParams() {
        init();
    }

    public AjaxParams(Map<String, String> source) {
        init();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public AjaxParams(String key, String value) {
        init();
        put(key, value);
    }

    /**
     * key1,value1,key2,value2
     *
     * @param keysAndValues
     */
    public AjaxParams(Object... keysAndValues) {
        init();
        int len = keysAndValues.length;
        if (len % 2 != 0)
            throw new IllegalArgumentException("Supplied arguments must be even");
        for (int i = 0; i < len; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }
    public void put(String key, int value) {
        put(key, value + "");
    }

    public void put(String key, long value) {
        put(key, value + "");
    }

    public void put(String key, File file) throws FileNotFoundException {
        put(key, new FileInputStream(file), file.getName());
    }

    public void put(String key, InputStream stream) {
        put(key, stream, null);
    }

    public void put(String key, InputStream stream, String fileName) {
        put(key, stream, fileName, null);
    }

    /**
     * 添加 inputStream 到请求中.
     *
     * @param key         the key name for the new param.
     * @param stream      the input stream to add.
     * @param fileName    the name of the file.
     * @param contentType the content type of the file, eg. application/json
     */
    public void put(String key, InputStream stream, String fileName, String contentType) {
        if (key != null && stream != null) {
            fileParams.put(key, new FileWrapper(stream, fileName, contentType));
        }
    }

    public void remove(String key) {
        urlParams.remove(key);
        fileParams.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String urlQuery = getUrlParamQuery();
        if(urlQuery!=null)
            sb.append(urlQuery);
        String fileQuery = getFileParamQuery();
        if(fileQuery!=null){
            if (sb.length() > 0)
                sb.append("&");
            sb.append(urlQuery);
        }
        return sb.toString();
    }

    private void init() {
        urlParams = new ConcurrentHashMap<String, String>();
        fileParams = new ConcurrentHashMap<String, FileWrapper>();
    }

    protected String getUrlParamQuery() {
        StringBuilder sb = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            if (sb.length() > 0)
                sb.append("&");
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

    protected String getFileParamQuery() {
        StringBuilder sb = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            if (sb.length() > 0)
                sb.append("&");
            sb.append(entry.getKey());
            sb.append("=");
            sb.append("FILE");
        }
        return sb.toString();
    }

    public String getParamString() {
        String query = getUrlParamQuery();
        if (query != null)
            try {
                return URLEncoder.encode(getUrlParamQuery(), Charsets.UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        return null;
    }

    public class FileWrapper {
        public InputStream inputStream;
        public String fileName;
        public String contentType;

        public FileWrapper(InputStream inputStream, String fileName, String contentType) {
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

    public ConcurrentHashMap<String, String> getUrlParams() {
        return urlParams;
    }

    public ConcurrentHashMap<String, FileWrapper> getFileParams() {
        return fileParams;
    }

}
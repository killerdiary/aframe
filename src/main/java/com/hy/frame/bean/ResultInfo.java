package com.hy.frame.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务器返回数据
 * Created by HeYan on 2015/10/17.
 */
public class ResultInfo {
    /**
     * 本地错误 默认
     */
    public static final int CODE_ERROR_DEFAULT = -250;
    /**
     * 网络错误
     */
    public static final int CODE_ERROR_NET = -251;
    /**
     * 解析错误
     */
    public static final int CODE_ERROR_DECODE = -252;
    private int requestCode;//请求码，接口编号
    private long qid;//队列ID
    private Object obj;//返回结果
    private int errorCode;//错误码
    private String msg;//描述
    private int requestType;
    private Map<String, String> maps;//其他

    /**
     * 请求码，接口编号
     */
    public int getRequestCode() {
        return requestCode;
    }

    /**
     * 请求码，接口编号
     */
    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    /**
     * 队列ID
     */
    public long getQid() {
        return qid;
    }

    /**
     * 队列ID
     */
    public void setQid(long qid) {
        this.qid = qid;
    }

    /**
     * 返回结果
     */
    public <T> T getObj() {
        return (T) obj;
    }

    /**
     * 返回结果
     */
    public void setObj(Object obj) {
        this.obj = obj;
    }

    /**
     * 错误码 250 本地错误 251 网络错误
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 错误码
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 描述
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 描述
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public void putValue(String key, String value) {
        if (maps == null)
            maps = new HashMap<>();
        maps.put(key, value);
    }

    public String getValue(String key) {
        if (maps != null) return maps.get(key);
        return null;
    }
}

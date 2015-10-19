package com.hy.frame.bean;

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
    private int qid;//队列ID
    private Object obj;//返回结果
    private int errorCode;//错误码
    private String msg;//描述

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
    public int getQid() {
        return qid;
    }

    /**
     * 队列ID
     */
    public void setQid(int qid) {
        this.qid = qid;
    }

    /**
     * 返回结果
     */
    public Object getObj() {
        return obj;
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
}

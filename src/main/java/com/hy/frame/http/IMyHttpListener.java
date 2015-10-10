package com.hy.frame.http;

public interface IMyHttpListener {
    /**
     * 请求成功
     * 
     * @param requestCode
     *            请求码
     * @param obj
     *            返回的数据
     * @param msg
     *            成功消息
     */
    void onRequestSuccess(int requestCode, Object obj, String msg);

    /**
     * 请求异常，服务器，网络故障
     * 
     * @param requestCode
     *            请求码
     * @param errorCode
     *            错误码 -1为服务器返回
     * @param msg
     *            错误消息
     */
    void onRequestError(int requestCode, int errorCode, String msg);

}
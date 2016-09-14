package com.hy.http;

import android.support.annotation.MainThread;

import com.hy.frame.bean.ResultInfo;

public interface IMyHttpListener {
    /**
     * 请求成功
     *
     * @param result 请求结果信息
     */
    @MainThread
    void onRequestSuccess(ResultInfo result);

    /**
     * 请求失败
     *
     * @param result 请求失败信息
     */
    @MainThread
    void onRequestError(ResultInfo result);

}
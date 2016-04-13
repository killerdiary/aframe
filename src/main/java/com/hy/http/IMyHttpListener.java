package com.hy.http;

import com.hy.frame.bean.ResultInfo;

public interface IMyHttpListener {
    /**
     * 请求成功
     *
     * @param result 请求结果信息
     */
    void onRequestSuccess(ResultInfo result);

    /**
     * 请求失败
     *
     * @param result 请求结果信息
     */
    void onRequestError(ResultInfo result);

}
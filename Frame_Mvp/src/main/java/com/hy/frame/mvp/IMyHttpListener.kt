package com.hy.frame.mvp

import android.support.annotation.MainThread
import com.hy.frame.bean.ResultInfo

interface IMyHttpListener {
    /**
     * 请求成功
     * @param result 请求结果信息
     */
    @MainThread
    fun onRequestSuccess(result: ResultInfo)

    /**
     * 请求失败
     * @param result 请求失败信息
     */
    @MainThread
    fun onRequestError(result: ResultInfo)

}
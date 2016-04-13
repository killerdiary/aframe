package com.hy.http;

import com.hy.frame.bean.ResultInfo;

/**
 * Success Listener
 * author HeYan
 * time 2016/4/13 15:04
 */
public abstract class ISuccessListener {
    private ResultInfo result;

    public ResultInfo getResult() {
        return result;
    }

    public void setResult(ResultInfo result) {
        this.result = result;
    }

    /**
     * 请求成功
     *
     * @param data 请求结果信息
     */
    abstract void onSuccess(String data);
}

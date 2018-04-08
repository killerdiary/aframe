package com.hy.frame.mvp

import com.hy.frame.app.IRetrofitManager

/**
 * BaseModel
 *
 * @author HeYan
 * @time 2018/4/6 17:54
 */
abstract class BaseModel(retrofitManager: IRetrofitManager?) : IBaseModel {
    protected var mRetrofitManager: IRetrofitManager? = retrofitManager

    override fun onDestroy() {
        mRetrofitManager = null
    }
}
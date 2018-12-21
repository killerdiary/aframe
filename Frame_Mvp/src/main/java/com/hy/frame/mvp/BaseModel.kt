package com.hy.frame.mvp

/**
 * BaseModel
 *
 * @author HeYan
 * @time 2018/4/6 17:54
 */
abstract class BaseModel(retrofitManager: IRetrofitManager) : IBaseModel {

    private var mRetrofitManager: IRetrofitManager? = retrofitManager

    override fun getRetrofitManager(): IRetrofitManager = mRetrofitManager!!

    override fun onDestroy() {
        mRetrofitManager?.onDestroy()
        mRetrofitManager = null
    }
}
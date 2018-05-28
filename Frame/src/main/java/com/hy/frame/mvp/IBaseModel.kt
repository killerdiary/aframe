package com.hy.frame.mvp

import com.hy.frame.app.IRetrofitManager

/**
 * MVP中Model需要实现的Interface
 *
 * @author HeYan
 * @time 2018/4/4 10:15
 */
interface IBaseModel {

    fun getRetrofitManager(): IRetrofitManager

    /**
     * destroy
     */
    fun onDestroy()
}
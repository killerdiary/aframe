package com.hy.frame.mvp.contract


/**
 * title 通用Contract
 * author heyan
 * time 19-1-18 上午11:56
 * desc 无
 */
interface ApiContract {
    interface IView : com.hy.frame.mvp.IBaseView {

    }

    interface IModel : com.hy.frame.mvp.IBaseModel, com.hy.frame.mvp.model.ApiService {

    }
}
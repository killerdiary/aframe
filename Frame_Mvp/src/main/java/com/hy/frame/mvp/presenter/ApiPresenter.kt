package com.hy.frame.mvp.presenter

import com.hy.frame.mvp.BasePresenter
import com.hy.frame.mvp.contract.ApiContract
import com.hy.frame.mvp.model.ApiModel

/**
 * title 无
 * author heyan
 * time 19-1-18 下午12:17
 * desc 无
 */
class ApiPresenter<out V : ApiContract.IView>(context: android.content.Context, view: V) : BasePresenter<V, ApiContract.IModel>(context, view, ApiModel()) {

}
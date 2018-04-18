package com.hy.frame.mvp

import android.content.Context
import android.support.annotation.StringRes

/**
 * BasePresenter
 *
 * @author HeYan
 * @time 2018/4/6 18:14
 */
abstract class BasePresenter<M : IBaseModel, V : IBaseView>(context: Context, view: V, model: M? = null) : IBasePresenter {
    protected var mContext: Context = context
    protected var mView: V = view
    protected var mModel: M? = model

    protected fun getString(@StringRes id: Int): String {
        return mContext.resources.getString(id)
    }

    protected fun getStrings(vararg ids: Int): String {
        val sb = StringBuilder()
        for (id in ids) {
            sb.append(getString(id))
        }
        return sb.toString()
    }

    override fun onDestroy() {
        this.mModel?.onDestroy()
        this.mModel = null
    }
}
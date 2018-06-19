package com.hy.frame.mvp

import android.content.Context
import androidx.annotation.StringRes

/**
 * BasePresenter
 *
 * @author HeYan
 * @time 2018/4/6 18:14
 */
abstract class BasePresenter<out M : IBaseModel, out V : IBaseView>(context: Context, view: V, model: M? = null) : IBasePresenter {
    private var mContext: Context? = context
    private var mView: V? = view
    private var mModel: M? = model

    override fun getContext(): Context = mContext!!

    protected fun getView(): V = mView!!

    protected fun getModel(): M = mModel!!

    protected fun getString(@StringRes id: Int): String {
        return getContext().resources.getString(id)
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
        this.mView = null
        this.mContext = null
    }
}
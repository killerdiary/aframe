package com.hy.frame.mvp

import android.annotation.SuppressLint
import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.support.annotation.StringRes

/**
 * BasePresenter
 *
 * @author HeYan
 * @time 2018/4/6 18:14
 */
@SuppressLint("RestrictedApi")
abstract class BasePresenter<out V : IBaseView, out M : IBaseModel>(context: Context, view: V, model: M? = null) : IBasePresenter {
    private var mContext: Context? = context
    private var mView: V? = view
    private var mModel: M? = model
    private var lifecycleObserver = GenericLifecycleObserver { _, event ->
        if (event != null && event == Lifecycle.Event.ON_DESTROY) {
            onDestroy()
        }
    }

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

    override fun getLifecycleObserver(): android.arch.lifecycle.LifecycleObserver = lifecycleObserver

    override fun onDestroy() {
        this.mModel?.onDestroy()
        this.mModel = null
        this.mView = null
        this.mContext = null
    }
}
package com.hy.frame.mvp

import com.hy.frame.ui.IBaseTemplateUI

abstract class BaseActivity<out P : IBasePresenter> : com.hy.frame.ui.BaseActivity(), IBaseView {

    private var mPresenter: P? = null//如果当前页面逻辑简单, Presenter 可以为 null

    protected fun getPresenter(): P? {
        if (mPresenter == null)
            mPresenter = buildPresenter()
        if (mPresenter != null)
            lifecycle.addObserver(mPresenter!!.getLifecycleObserver())
        return mPresenter
    }

    override fun getTemplateUI(): IBaseTemplateUI  = this

    /**
     * 如果当前页面逻辑简单, Presenter 可以为 null
     */
    protected abstract fun buildPresenter(): P?
}
package com.hy.frame.mvp

abstract class BaseActivity<out P : IBasePresenter> : com.hy.frame.ui.BaseActivity(), IBaseView {

    private var mPresenter: P? = null//如果当前页面逻辑简单, Presenter 可以为 null

    protected fun getPresenter(): P? {
        if (mPresenter == null)
            mPresenter = buildPresenter()
        if (mPresenter != null)
            lifecycle.addObserver(mPresenter!!)
        return mPresenter
    }

    /**
     * 如果当前页面逻辑简单, Presenter 可以为 null
     */
    protected abstract fun buildPresenter(): P?

    /**
     * 获取模板[com.hy.frame.ui.IBaseTemplateView]
     */
    override fun getTemplateView(): com.hy.frame.ui.IBaseTemplateView = this

    /**
     * 获取模板[com.hy.frame.ui.ITemplateControl]
     */
    override fun getTemplateControl(): com.hy.frame.ui.ITemplateControl? = getTemplateView().getTemplateControl()

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
    }
}
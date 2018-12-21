package com.hy.frame.mvp

abstract class BaseActivity<out P : IBasePresenter> : com.hy.frame.ui.BaseActivity(){

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


    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.onDestroy()
    }
}
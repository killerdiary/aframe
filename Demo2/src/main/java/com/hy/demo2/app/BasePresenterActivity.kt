package com.hy.demo2.app

import android.content.pm.ActivityInfo
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.hy.demo2.R
import com.hy.frame.mvp.IBasePresenter

abstract class BasePresenterActivity<out P : IBasePresenter> : com.hy.frame.mvp.BaseActivity<P>(){

    override fun isPermissionDenied(): Boolean = false
    override fun isSingleLayout(): Boolean = false
    override fun isTranslucentStatus(): Boolean = false
    override fun getScreenOrientation(): Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    /**
     * 初始化头,默认返回按钮
     * @param title 标题
     * @param right 右边图标
     */
    protected fun initHeaderBack(@StringRes title: Int, @DrawableRes right: Int = 0) {
        getTemplateControl()?.setHeaderLeft(R.drawable.v_back)
        getTemplateControl()?.setHeaderRight(right)
        setTitle(title)
    }

    /**.
     * 初始化头,默认返回按钮
     * @param title 标题
     * @param right 右边文字
     */
    protected fun initHeaderBackTxt(@StringRes title: Int, @StringRes right: Int) {
        getTemplateControl()?.setHeaderLeft(R.drawable.v_back)
        getTemplateControl()?.setHeaderRightTxt(getString(right))
        setTitle(title)
    }

}
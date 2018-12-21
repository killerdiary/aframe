package com.hy.demo2.app

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.hy.demo2.R

abstract class BaseFragment : com.hy.frame.ui.BaseFragment() {

    override fun isSingleLayout(): Boolean = false

    override fun onRestart() {

    }

    override fun sendMsg(flag: Int, obj: Any?) {

    }

    /**
     * 初始化头,默认返回按钮
     * @param title 标题
     * @param right 右边图标
     */
    protected fun initHeaderBack(@StringRes title: Int, @DrawableRes right: Int = 0) {
        getTemplateControl()?.setHeaderLeft(R.drawable.v_back)
        getTemplateControl()?.setHeaderRight(right)
        getTemplateControl()?.setTitle(getString(title))
    }

    /**.
     * 初始化头,默认返回按钮
     * @param title 标题
     * @param right 右边文字
     */
    protected fun initHeaderBackTxt(@StringRes title: Int, @StringRes right: Int) {
        getTemplateControl()?.setHeaderLeft(R.drawable.v_back)
        getTemplateControl()?.setHeaderRightTxt(getString(right))
        getTemplateControl()?.setTitle(getString(title))
    }
}
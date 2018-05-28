package com.hy.app.common

import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.hy.app.R
import com.hy.frame.bean.ResultInfo
import com.hy.frame.mvp.IBasePresenter
import com.hy.http.IMyHttpListener

/**
 * 父类Activity
 *
 * @author HeYan
 * @time 2014-7-18 下午2:53:55
 */
abstract class BaseActivity : com.hy.frame.app.BaseActivity<IBasePresenter>(), IMyHttpListener {
    override fun isPortrait(): Boolean = true
    override fun isSingleLayout(): Boolean = false
    override fun isTranslucentStatus(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return true
        return false
    }

    override fun isPermissionDenied(): Boolean = false

    //protected abstract fun getPresenter(): P?

    var mSavedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //setTranslucentStatus(false);
        mSavedInstanceState = savedInstanceState
        super.onCreate(savedInstanceState)
    }

    /**
     * 初始化头,默认返回按钮
     * @param title 标题
     * @param right 右边图标
     */
    protected fun initHeaderBack(@StringRes title: Int, @DrawableRes right: Int = 0) {
        setHeaderLeft(R.drawable.v_back)
        setHeaderRight(right)
        setTitle(title)
    }

    /**.
     * 初始化头,默认返回按钮
     * @param title 标题
     * @param right 右边文字
     */
    protected fun initHeaderBackTxt(@StringRes title: Int, @StringRes right: Int) {
        setHeaderLeft(R.drawable.v_back)
        setHeaderRightTxt(right)
        setTitle(title)
    }

    override fun onRequestSuccess(result: ResultInfo) {

    }

    override fun onRequestError(result: ResultInfo) {

    }

    override fun getRequestListener(): IMyHttpListener? = this

    override fun buildPresenter(): IBasePresenter? = null
}

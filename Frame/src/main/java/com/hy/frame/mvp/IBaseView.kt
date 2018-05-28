package com.hy.frame.mvp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hy.frame.R
import com.hy.frame.app.IBaseApplication
import com.hy.http.IMyHttpListener

/**
 * MVP中View需要实现的Interface
 *
 * @author HeYan
 * @time 2018/4/3 12:17
 */
interface IBaseView {
    fun getCurContext(): Context

    fun getCurApp(): IBaseApplication

    fun getRequestListener(): IMyHttpListener?

    fun showToast(msg: String?)
    /**
     * 显示加载中
     */
    fun showLoading(resId: Int = R.string.loading)

    /**
     * 显示加载中
     */
    fun showLoading(msg: String)

    /**
     * 显示加载中
     */
    fun showLoadingDialog(resId: Int = R.string.loading)

    /**
     * 显示加载中
     */
    fun showLoadingDialog(msg: String)

    /**
     * 隐藏加载中
     */
    fun hideLoadingDialog()

    /**
     * 显示没有数据或错误页
     */
    fun showNoData(resId: Int = R.string.hint_nodata, drawId: Int = R.drawable.v_warn)

    /**
     * 显示没有数据或错误页
     */
    fun showNoData(msg: String, drawId: Int = R.drawable.v_warn)

    /**
     * 显示内容View
     */
    fun showCView()

    fun startAct(cls: Class<*>, bundle: Bundle? = null, intent: Intent? = null)

    fun startActForResult(cls: Class<*>, requestCode: Int, bundle: Bundle? = null, intent: Intent? = null)
}
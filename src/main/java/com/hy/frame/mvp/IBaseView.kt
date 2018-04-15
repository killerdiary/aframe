package com.hy.frame.mvp

import android.content.Context
import com.hy.frame.R
import com.hy.frame.app.IBaseApplication

/**
 * MVP中View需要实现的Interface
 *
 * @author HeYan
 * @time 2018/4/3 12:17
 */
interface IBaseView {
    fun getCurContext(): Context

    fun getCurApp(): IBaseApplication

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
//    /**
//     * 请求数据
//     */
//    fun requestData()

    /**
     * 更新UI
     */
    fun updateUI()

    /**
     * 显示没有数据或错误页
     */
    fun showNoData(resId: Int = R.string.hint_nodata, drawId: Int = R.mipmap.ic_nodata)

    /**
     * 显示没有数据或错误页
     */
    fun showNoData(msg: String, drawId: Int = R.mipmap.ic_nodata)

    /**
     * 显示内容View
     */
    fun showCView()
}
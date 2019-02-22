package com.hy.frame.ui

import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.FrameLayout
import com.hy.frame.R

/**
 * 公有模板控制器interface
 * @author
 * @time 18-10-22 上午10:31
 */
interface ITemplateControl {

    fun init(templateView: IBaseTemplateUI, mToolbar: Toolbar?, mFlyMain: FrameLayout?)

    /**
     * 设置标题
     */
    fun setTitle(title: CharSequence?)

    /**
     * 设置头部左侧图标
     */
    fun setHeaderLeft(@DrawableRes left: Int)

    /**
     * 设置头部左侧文本
     */
    fun setHeaderLeftTxt(left: String?)

    /**
     * 设置头部右侧图标
     */
    fun setHeaderRight(@DrawableRes right: Int)

    /**
     * 设置头部右侧文本
     */
    fun setHeaderRightTxt(right: String?)

    /**
     * 添加头部右侧图标
     */
    fun addHeaderRight(@DrawableRes right: Int, @IdRes id: Int)

    /**
     * 添加头部右侧网络图标
     */
    fun addHeaderRightPath(rightPath: String?, @IdRes id: Int)

    /**
     * 显示提示
     */
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
     * 隐藏头部
     */
    fun hideHeader()

    fun getHeader(): View?

    fun getHeaderTitle(): View?

    fun getHeaderLeft(): View?

    fun getHeaderRight(): View?

    fun getMainView(): View?

    /**
     * 资源释放
     */
    fun onDestroy()
}
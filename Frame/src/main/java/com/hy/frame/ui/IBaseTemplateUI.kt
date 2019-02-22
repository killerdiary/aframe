package com.hy.frame.ui

import android.content.Intent
import android.os.Bundle
import com.hy.frame.app.IBaseApplication

/**
 * 供 Acitivity和Fragment使用
 */
interface IBaseTemplateUI : IBaseUI {
    /**
     * 是否启用唯一布局，否者使用公有模板[ITemplateControl]
     */
    fun isSingleLayout(): Boolean

    /**
     * 获取模板[ITemplateControl]
     */
    fun getTemplateControl(): ITemplateControl?

    /**
     * 是否开启透明状态栏
     */
    fun isTranslucentStatus(): Boolean

    /**
     * 状态栏高度
     */
    fun getStatusBarHeight(): Int

    fun getCurApp(): IBaseApplication

    fun getCurActivity(): AppCompatActivity

    /**
     * 头-左边图标点击
     */
    fun onLeftClick()

    /**
     * 头-右边图标点击
     */
    fun onRightClick()

    /**
     * 加载View点击，用于加载失败后重试
     */
    fun onLoadViewClick()

    fun startAct(cls: Class<*>, bundle: Bundle? = null, intent: Intent? = null)

    fun startActForResult(cls: Class<*>, requestCode: Int, bundle: Bundle? = null, intent: Intent? = null)
}
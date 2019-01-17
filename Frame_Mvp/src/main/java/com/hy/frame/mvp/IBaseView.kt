package com.hy.frame.mvp

import com.hy.frame.ui.ITemplateControl

/**
 * MVP中View需要实现的Interface
 *
 * @author HeYan
 * @time 2018/4/3 12:17
 */
interface IBaseView {
    /**
     * 获取模板[com.hy.frame.ui.IBaseTemplateView]
     */
    fun getTemplateView(): com.hy.frame.ui.IBaseTemplateView
    
    /**
     * 获取模板[ITemplateControl]
     */
    fun getTemplateControl(): ITemplateControl?


}
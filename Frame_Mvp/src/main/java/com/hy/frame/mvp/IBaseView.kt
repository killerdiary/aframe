package com.hy.frame.mvp

/**
 * MVP中View需要实现的Interface
 *
 * @author HeYan
 * @time 2018/4/3 12:17
 */
interface IBaseView {
    /**
     * 获取模板[com.hy.frame.ui.IBaseTemplateUI]
     */
    fun getTemplateUI(): com.hy.frame.ui.IBaseTemplateUI

    /**
     * 获取模板[com.hy.frame.ui.ITemplateControl]
     */
    fun getTemplateControl(): com.hy.frame.ui.ITemplateControl?


}
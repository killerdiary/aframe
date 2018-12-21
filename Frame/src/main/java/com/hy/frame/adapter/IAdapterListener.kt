package com.hy.frame.adapter

import android.view.View

/**
 * IAdapterListener Click
 * @author HeYan
 * @time 2017/5/23 9:57
 */
interface IAdapterListener<in T> {

    /**
     * 点击事件
     * @param v
     * @param item
     * @param position
     */
    fun onViewClick(v: View, item: T, position: Int)
}

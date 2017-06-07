package com.hy.frame.adapter

import android.view.View

/**
 * IAdapterLongListener Long Click
 * @author HeYan
 * @time 2017/5/23 9:56
 */
interface IAdapterLongListener<in T> : IAdapterListener<T> {

    /**
     * 点击事件

     * @param v
     *
     * @param item
     *
     * @param position
     */
    fun onViewLongClick(v: View, item: T, position: Int)
}

package com.hy.frame.adapter;

import android.view.View;

/**
 * IAdapterLongListener Long Click
 *
 * @author HeYan
 * @time 2017/5/23 9:56
 */
public interface IAdapterLongListener<T> extends IAdapterListener<T> {

    /**
     * 点击事件
     *
     * @param v
     * @param item
     * @param position
     */
    void onViewLongClick(View v, T item, int position);
}

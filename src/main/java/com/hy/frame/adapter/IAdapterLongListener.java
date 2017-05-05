package com.hy.frame.adapter;

import android.view.View;

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

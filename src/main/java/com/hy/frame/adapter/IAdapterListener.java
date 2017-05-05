package com.hy.frame.adapter;

import android.view.View;

public interface IAdapterListener<T> {
    /**
     * 点击事件
     * 
     * @param v
     * @param item
     * @param position
     */
    void onViewClick(View v, T item, int position);
}

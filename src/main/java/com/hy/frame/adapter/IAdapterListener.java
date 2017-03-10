package com.hy.frame.adapter;

import android.view.View;

public interface IAdapterListener {
    /**
     * 点击事件
     * 
     * @param v
     * @param obj
     * @param position
     */
    void onViewClick(View v, Object obj, int position);
}

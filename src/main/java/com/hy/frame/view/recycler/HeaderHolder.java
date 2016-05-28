package com.hy.frame.view.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * HeaderHolder
 *
 * @author HeYan
 * @time 2016/5/27 16:35
 */
public class HeaderHolder extends RecyclerView.ViewHolder {
    public HeaderHolder(View v) {
        super(v);
        if (v.getLayoutParams() == null) {
            v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}

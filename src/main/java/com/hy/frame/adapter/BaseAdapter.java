package com.hy.frame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    private Context context;
    private IAdapterListener listener;

    public BaseAdapter(Context context) {
        this.context = context;
    }

    protected void setContext(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    protected IAdapterListener getListener() {
        return listener;
    }

    protected View inflate(int resId) {
        return LayoutInflater.from(context).inflate(resId, null);
    }

    public void setListener(IAdapterListener listener) {
        this.listener = listener;
    }

    @Deprecated
    public void setOnClickListener(View v, T t, int position) {
        if (getListener() != null)
            v.setOnClickListener(new ViewOnClick(getListener(), t, position));
    }

    public void setOnLongClickListener(View v, T t, int position) {
        if (getListener() != null)
            v.setOnLongClickListener(new ViewOnLongClick(getListener(), t, position));
    }

    @SuppressWarnings("unchecked")
    protected <V> V getView(View v, int resId) {
        return (V) v.findViewById(resId);
    }
}

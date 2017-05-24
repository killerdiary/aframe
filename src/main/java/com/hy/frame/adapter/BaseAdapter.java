package com.hy.frame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * BaseAdapter for ListView or GridView
 *
 * @author HeYan
 * @time 2017/5/23 9:35
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    private Context context;
    private IAdapterListener<T> listener;

    public BaseAdapter(Context context) {
        this.context = context;
    }

    public BaseAdapter(Context context, IAdapterListener<T> listener) {
        this.context = context;
        this.listener = listener;
    }

    protected void setContext(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

    protected IAdapterListener<T> getListener() {
        return listener;
    }

    protected View inflate(int resId) {
        return LayoutInflater.from(context).inflate(resId, null);
    }

    public void setListener(IAdapterListener<T> listener) {
        this.listener = listener;
    }

    @Deprecated
    public void setOnClickListener(View v, T t, int position) {
        if (listener != null)
            v.setOnClickListener(new ViewOnClick(getListener(), t, position));
    }

    public void setOnLongClickListener(View v, T t, int position) {
        if (listener != null && listener instanceof IAdapterLongListener)
            v.setOnLongClickListener(new ViewOnLongClick(getListener(), t, position));
    }

    @SuppressWarnings("unchecked")
    protected <V> V getView(View v, int resId) {
        return (V) v.findViewById(resId);
    }

    class ViewOnClick implements android.view.View.OnClickListener {
        T obj;
        int position;
        IAdapterListener<T> listener;

        public ViewOnClick(IAdapterListener<T> listener, T obj, int position) {
            this.obj = obj;
            this.position = position;
            this.listener = listener;
        }

        @Override
        public void onClick(android.view.View v) {
            if (listener != null) {
                listener.onViewClick(v, obj, position);
            }
        }

    }

    class ViewOnLongClick implements android.view.View.OnLongClickListener {
        T obj;
        int position;
        IAdapterLongListener<T> listener;

        public ViewOnLongClick(IAdapterListener<T> listener, T obj, int position) {
            this.obj = obj;
            this.position = position;
            this.listener = (IAdapterLongListener<T>) listener;
        }

        @Override
        public boolean onLongClick(android.view.View v) {
            if (listener != null) {
                listener.onViewLongClick(v, obj, position);
            }
            return false;
        }

    }
}

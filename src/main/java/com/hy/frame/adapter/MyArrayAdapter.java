package com.hy.frame.adapter;

import android.content.Context;

/**
 * MyArrayAdapter
 *
 * @author HeYan
 * @time 2017/5/23 10:09
 */
public abstract class MyArrayAdapter<T> extends BaseAdapter<T> {
    private T[] datas;

    public MyArrayAdapter(Context context, T[] datas) {
        super(context);
        this.datas = datas;
    }

    public MyArrayAdapter(Context context, T[] datas, IAdapterListener<T> listener) {
        super(context, listener);
        this.datas = datas;
    }

    public void setList(T[] datas) {
        this.datas = datas;
    }

    public void refresh(T[] datas) {
        this.datas = datas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.length;
    }

    @Override
    public T getItem(int position) {
        return datas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

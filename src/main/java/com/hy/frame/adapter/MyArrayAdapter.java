package com.hy.frame.adapter;

import android.content.Context;

public abstract class MyArrayAdapter<T> extends BaseAdapter<T> {
	private T[] datas;

	public MyArrayAdapter(Context context, T[] datas) {
		super(context);
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

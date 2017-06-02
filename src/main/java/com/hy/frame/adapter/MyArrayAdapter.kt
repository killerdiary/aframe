package com.hy.frame.adapter

import android.content.Context

/**
 * MyArrayAdapter

 * @author HeYan
 *
 * @time 2017/5/23 10:09
 */
abstract class MyArrayAdapter<T> : BaseAdapter<T> {
    private var datas: Array<T>? = null

    constructor(context: Context, datas: Array<T>) : super(context) {
        this.datas = datas
    }

    constructor(context: Context, datas: Array<T>, listener: IAdapterListener<T>) : super(context, listener) {
        this.datas = datas
    }

    fun setList(datas: Array<T>) {
        this.datas = datas
    }

    fun refresh(datas: Array<T>) {
        this.datas = datas
        this.notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (datas == null) 0 else datas!!.size
    }

    override fun getItem(position: Int): T {
        return datas!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}

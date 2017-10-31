package com.hy.frame.adapter

import android.content.Context

/**
 * MyBaseAdapter for ListView or GridView
 * @author HeYan
 * @time 2017/5/23 10:07
 */
abstract class MyBaseAdapter<T> : BaseAdapter<T> {
    private var datas: List<T>? = null

    constructor(context: Context, datas: MutableList<T>) : super(context) {
        this.datas = datas
    }

    constructor(context: Context, datas: MutableList<T>, listener: IAdapterListener<T>) : super(context, listener) {
        this.datas = datas
    }

    fun setList(datas: List<T>) {
        this.datas = datas
    }

    fun refresh(datas: List<T>) {
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

package com.hy.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.hy.app.R
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.bean.MenuInfo
import com.hy.frame.widget.recycler.BaseHolder
import com.hy.frame.widget.recycler.BaseRecyclerAdapter

/**
 * RecyclerAdapter
 * @author HeYan
 * @time 2017/9/25 12:03
 */
class RecyclerAdapter(context: Context, datas: MutableList<MenuInfo>?, listener: IAdapterListener<MenuInfo>) : BaseRecyclerAdapter<MenuInfo>(context, datas, listener) {

    override fun createView(parent: ViewGroup, viewType: Int): BaseHolder {
        return ItemHolder(inflate(R.layout.item_list_recycler))
    }

    override fun bindViewData(holder: BaseHolder, position: Int) {
        val item = getItem(position)
        //h.txtTitle!!.setText(item.title)
    }

    /**
     * 自定义的ViewHolder，持有每个Item的的所有界面元素
     * static不是说只存在1个实例，而是可以访问外部类的静态变量，final修饰类则是不让该类继承
     */
    internal inner class ItemHolder(v: View) : BaseClickHolder(v) {
        val txtTitle: TextView = setOnClickListener(R.id.list_recycler_i_txtTitle)!!
    }
}
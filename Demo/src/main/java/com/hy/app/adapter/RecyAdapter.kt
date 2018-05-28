package com.hy.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hy.frame.widget.recycler.BaseHolder
import com.hy.frame.widget.recycler.BaseRecyclerAdapter


/**
 * 描述
 *
 * @author HeYan
 * @time 2016/5/27 14:33
 */
class RecyAdapter(context: Context, datas: MutableList<String>?) : BaseRecyclerAdapter<String>(context, datas, null) {

    override fun bindViewData(holder: BaseHolder, position: Int) {
        holder as ItemHolder
        holder.txtTitle.text = getItem(position)
    }

    override fun createView(parent: ViewGroup, viewType: Int): BaseHolder {
        return ItemHolder(inflate(android.R.layout.simple_list_item_1))
    }

    /**
     * 自定义的ViewHolder，持有每个Item的的所有界面元素
     * static不是说只存在1个实例，而是可以访问外部类的静态变量，final修饰类则是不让该类继承
     */
    internal inner class ItemHolder(v: View) : BaseClickHolder(v) {
        var txtTitle: TextView = findViewById(android.R.id.text1)!!

        init {
            v.setOnClickListener(this)
        }

    }
}

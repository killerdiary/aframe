package com.hy.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.hy.app.R
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.widget.recycler.BaseHolder
import com.hy.frame.widget.recycler.BaseRecyclerAdapter

/**
 * SwipeAdapter
 *
 * @author HeYan
 * @time 2017/5/10 16:56
 */
class SwipeAdapter(context: Context, datas: MutableList<String>?, listener: IAdapterListener<String>) : BaseRecyclerAdapter<String>(context, datas, listener) {

    override fun createView(parent: ViewGroup, viewType: Int): BaseHolder {
        return ItemHolder(inflate(R.layout.item_swipe))
    }

    override fun bindViewData(holder: BaseHolder, position: Int) {
        holder as ItemHolder
        val item = getItem(position)
        holder.txtTitle.text = item
    }

    inner class ItemHolder(v: View) : BaseClickHolder(v) {
        val txtTitle: TextView = findViewById(R.id.swipe_i_txtTitle)!!
        val btnDelete: Button = setOnClickListener(R.id.swipe_i_btnDelete)!!
    }
}

package com.hy.app.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hy.app.R
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.widget.recycler.BaseHolder
import com.hy.frame.widget.recycler.BaseRecyclerAdapter

/**
 * FloatAdapter
 * @author HeYan
 * @time 2017/10/27 10:49
 */
class FloatAdapter(context: Context, datas: MutableList<String>?, listener: IAdapterListener<String>) : BaseRecyclerAdapter<String>(context, datas, listener) {

    override fun getCurViewType(position: Int): Int {
        return if (position % 3 == 0) TYPE_GROUP else super.getCurViewType(position)
    }

    override fun createView(parent: ViewGroup, viewType: Int): BaseHolder {
        if (viewType == TYPE_GROUP)
            return GroupHolder(inflate(R.layout.item_list_simple))
        return ItemHolder(inflate(R.layout.item_list_simple))
    }

    override fun bindViewData(holder: BaseHolder, position: Int) {
        val item = getItem(position)
        when (holder.itemViewType) {
            TYPE_GROUP -> {
                (holder as GroupHolder).txtTitle.text = item
            }
            else -> {
                (holder as ItemHolder).txtTitle.text = item
            }
        }

    }

    open inner class ItemHolder(v: View) : BaseClickHolder(v) {
        val txtTitle: TextView = setOnClickListener(R.id.list_simple_i_txtTitle)!!
    }

    inner class GroupHolder(v: View) : ItemHolder(v) {

        init {
            txtTitle.setBackgroundColor(Color.LTGRAY)
        }
    }

    companion object {
        val TYPE_GROUP = 11
    }
}
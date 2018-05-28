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
 * 主页
 * author HeYan
 * time 2016/8/19 11:04
 */
class MenuAdapter(context: Context, datas: MutableList<MenuInfo>?, listener: IAdapterListener<MenuInfo>) : BaseRecyclerAdapter<MenuInfo>(context, datas, listener) {

    public override fun bindViewData(holder: BaseHolder, position: Int) {
        holder as ItemHolder
        val item = getItem(position)
        holder.txtTitle.setText(item.title)
    }

    override fun createView(parent: ViewGroup, viewType: Int): BaseHolder {
        return ItemHolder(inflate(R.layout.item_menu))
    }

    internal inner class ItemHolder(v: View) : BaseClickHolder(v) {

        val txtTitle: TextView = setOnClickListener(R.id.main_i_txtTitle)!!

        init {
            //v.layoutParams.width = HyUtil.dip2px(context, 120F)
            //v.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }
}
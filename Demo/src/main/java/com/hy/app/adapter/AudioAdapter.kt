package com.hy.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hy.app.bean.AudioInfo
import com.hy.frame.util.HyUtil
import com.hy.frame.widget.recycler.BaseHolder
import com.hy.frame.widget.recycler.BaseRecyclerAdapter

/**
 * @author HeYan
 * @title
 * @time 2015/10/21 17:50
 */
class AudioAdapter(context: Context, datas: MutableList<AudioInfo>) : BaseRecyclerAdapter<AudioInfo>(context, datas) {
    override fun createView(parent: ViewGroup, viewType: Int): BaseHolder {
        return BaseHolder(View(context))
    }

    override fun bindViewData(holder: BaseHolder, position: Int) {

    }
//    override fun getView(position: Int, v: View?, parent: ViewGroup): View {
//        var v = v
//        val cache: ViewCache
//        if (v == null) {
//            v = inflate(android.R.layout.simple_list_item_1)
//            cache = ViewCache()
//            cache.txtTitle = HyUtil.findViewById(android.R.id.text1, v)
//            v.tag = cache
//        } else
//            cache = v.tag as ViewCache
//        val item = getItem(position)
//        val sb = StringBuilder()
//        sb.append("地址：\n")
//        sb.append("\t")
//        sb.append(item.path)
//        sb.append("\n大小：")
//        sb.append(item.kb.toString() + "kb")
//        cache.txtTitle!!.text = sb.toString()
//        return v
//    }

    internal inner class ViewCache {
        var txtTitle: TextView? = null
    }
}

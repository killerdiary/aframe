package com.hy.frame.widget.recycler

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes

/**
 * BaseHolder
 * @author HeYan
 * @time 2017/9/25 14:35
 */
open class BaseHolder(v: View) : RecyclerView.ViewHolder(v) {

    init {
        if (v.layoutParams == null) {
            v.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    fun <V : View> findViewById(@IdRes id: Int, parent: View? = null): V? {
        return parent?.findViewById(id) ?: itemView.findViewById(id)
    }
}
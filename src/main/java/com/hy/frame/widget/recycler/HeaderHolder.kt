package com.hy.frame.widget.recycler

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * HeaderHolder
 * @author HeYan
 * @time 2016/5/27 16:35
 */
@Deprecated("")
class HeaderHolder(v: View) : RecyclerView.ViewHolder(v) {
    init {
        if (v.layoutParams == null) {
            v.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}

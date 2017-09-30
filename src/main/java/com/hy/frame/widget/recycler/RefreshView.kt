package com.hy.frame.widget.recycler

import android.view.View
import com.hy.frame.R
import com.hy.frame.widget.WaterDropView

/**
 * RefreshView
 * @author HeYan
 * @time 2017/9/27 10:40
 */
class RefreshView(var v: View) {

    private val vWater = v.findViewById<WaterDropView>(R.id.recycler_refreshview_i_vWater)!!

    /**
     * 完成的百分比
     * @param percent between[0,1]
     */
    fun updateComleteState(percent: Float) {
        vWater.updateComleteState(percent)
    }

    fun clear() {
        vWater.createAnimator()
    }

    interface ILoadMoreListener {
        fun onLoadMore()
    }
}
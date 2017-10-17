package com.hy.frame.widget.recycler

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hy.frame.widget.recycler.BaseRecyclerAdapter

/**
 * ItemDecoration for GridLayoutManager
 * @author HeYan
 * @time 2017/9/25 14:57
 */
open class LinearItemDecoration : BaseItemDecoration {

    protected var orientation: Int = LinearLayoutManager.VERTICAL

    constructor(rcyList: RecyclerView, divider: Int, color: Int) : super(rcyList, divider, color)

    constructor(rcyList: RecyclerView, divider: Int, drawable: Drawable) : super(rcyList, divider, drawable)

    init {
        orientation = (rcyList.layoutManager as LinearLayoutManager).orientation
    }

    override fun drawDivider(canvas: Canvas) {
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        val childCount = rcyList.childCount
        for (i in 0 until childCount) {
            val child = rcyList.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val viewType = adapter?.getItemViewType(params.viewAdapterPosition) ?: 0
            if (viewType == BaseRecyclerAdapter.TYPE_ITEM) {
                if (orientation == LinearLayoutManager.VERTICAL) {
                    left = child.left
                    top = child.bottom
                    right = child.right
                    bottom = top + divider
                } else {
                    left = child.right
                    top = child.top
                    right = left + divider
                    bottom = child.bottom
                }
                if (drawable != null) {
                    drawable?.setBounds(left, top, right, bottom)
                    drawable?.draw(canvas)
                } else if (mPaint != null)
                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
            }
        }
    }

    override fun configureItemOutRect(outRect: Rect, view: View) {
        val params = view.layoutParams as RecyclerView.LayoutParams
        val viewAdapterPosition = params.viewAdapterPosition
        if (adapter != null && adapter!!.getItemViewType(viewAdapterPosition) == BaseRecyclerAdapter.TYPE_ITEM) {
            val position = adapter!!.getCurPosition(viewAdapterPosition)
            if (orientation == LinearLayoutManager.VERTICAL)
                outRect.set(paddingLeft, if (paddingTop > 0 && position == 0) paddingTop else 0, paddingRight, if (paddingBottom > 0 && position == adapter!!.getDataCount() - 1) paddingBottom + divider else divider)
            else
                outRect.set(if (paddingLeft > 0 && position == 0) paddingTop else 0, paddingTop, divider, paddingBottom)
        } else
            outRect.set(0, 0, 0, 0)
    }
}
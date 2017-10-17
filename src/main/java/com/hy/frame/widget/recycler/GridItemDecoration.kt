package com.hy.frame.widget.recycler

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hy.frame.widget.recycler.BaseRecyclerAdapter

/**
 * ItemDecoration for GridLayoutManager
 * @author HeYan
 * @time 2017/9/25 14:57
 */
class GridItemDecoration : LinearItemDecoration {

    private var spanCount = -1

    constructor(rcyList: RecyclerView, divider: Int, color: Int) : super(rcyList, divider, color)

    constructor(rcyList: RecyclerView, divider: Int, drawable: Drawable) : super(rcyList, divider, drawable)

    init {
        spanCount = (rcyList.layoutManager as GridLayoutManager).spanCount
    }

    override fun build(): GridItemDecoration {
        if (spanCount > 1)
            if (orientation == LinearLayoutManager.VERTICAL && paddingLeft > 0 && rcyList.paddingLeft == 0)
                rcyList.setPadding(paddingLeft, rcyList.paddingTop, rcyList.paddingTop, rcyList.paddingBottom)
            else if (orientation == LinearLayoutManager.HORIZONTAL && paddingTop > 0 && rcyList.paddingTop == 0)
                rcyList.setPadding(rcyList.paddingLeft, paddingTop, rcyList.paddingTop, rcyList.paddingBottom)
        return this
    }

    override fun drawDivider(canvas: Canvas) {
        if (spanCount == 1) {
            super.drawDivider(canvas)
            return
        }
        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        var vLeft = 0
        var vTop = 0
        var vRight = 0
        var vBottom = 0
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
                    vLeft = child.right
                    vTop = child.top
                    vRight = vLeft + dividerVertical
                    vBottom = child.bottom + divider
                } else {
                    left = child.left
                    top = child.bottom
                    right = child.right
                    bottom = top + divider
                    vLeft = child.right
                    vTop = child.top
                    vRight = vLeft + dividerVertical
                    vBottom = child.bottom + divider
                }
                if (drawable != null) {
                    drawable?.setBounds(left, top, right, bottom)
                    drawable?.draw(canvas)
                    drawable?.setBounds(vLeft, vTop, vRight, vBottom)
                    drawable?.draw(canvas)
                } else if (mPaint != null) {
                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
                    canvas.drawRect(vLeft.toFloat(), vTop.toFloat(), vRight.toFloat(), vBottom.toFloat(), mPaint)
                }
            }
        }
    }

    override fun configureItemOutRect(outRect: Rect, view: View) {
        if (spanCount == 1) {
            super.configureItemOutRect(outRect, view)
            return
        }
        val params = view.layoutParams as RecyclerView.LayoutParams
        val viewAdapterPosition = params.viewAdapterPosition
        if (adapter != null && adapter!!.getItemViewType(viewAdapterPosition) == BaseRecyclerAdapter.TYPE_ITEM) {
            val position = adapter!!.getCurPosition(viewAdapterPosition)
            val dataCount = adapter!!.getDataCount()
            if (orientation == LinearLayoutManager.VERTICAL)
                outRect.set(
                        0,
                        if (paddingTop > 0 && position < spanCount) paddingTop else 0,
                        dividerVertical,
                        if (paddingBottom > 0 && (dataCount % spanCount == 0 && position >= (dataCount - spanCount) || dataCount % spanCount != 0 && position >= dataCount / spanCount * spanCount)) paddingBottom + divider else divider)
            else {
                //outRect.set(if (paddingLeft > 0 && position == 0) paddingTop else 0, paddingTop, divider, paddingBottom)
                outRect.set(
                        if (paddingLeft > 0 && position < spanCount) paddingLeft else 0,
                        0,
                        dividerVertical,
                        divider)

            }
        } else
            outRect.set(0, 0, 0, 0)
    }
}
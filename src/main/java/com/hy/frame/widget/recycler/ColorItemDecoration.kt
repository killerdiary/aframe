package com.hy.frame.widget.recycler

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * ColorItemDecoration
 * @author HeYan
 * @time 2017/9/25 14:57
 */
class ColorItemDecoration : RecyclerView.ItemDecoration {
    private val dividerHeight: Int
    private var dividerVerticalWidth: Int = 0
    private val drawable: Drawable?
    private val mPaint: Paint?
    private var rcyList: RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    constructor(dividerHeight: Int, color: Int, alpha: Int = 255) {
        this.dividerHeight = dividerHeight
        this.drawable = null
        this.mPaint = Paint()
        this.mPaint.color = color
        this.mPaint.alpha = alpha
        this.mPaint.flags = Paint.ANTI_ALIAS_FLAG
    }

    constructor(dividerHeight: Int, drawable: Drawable) {
        this.dividerHeight = dividerHeight
        this.drawable = drawable
        this.mPaint = null
    }

    fun setDividerVerticalWidth(dividerVerticalWidth: Int): ColorItemDecoration {
        this.dividerVerticalWidth = dividerVerticalWidth
        return this
    }

    private fun init(parent: RecyclerView) {
        if (rcyList == null) {
            rcyList = parent
            adapter = parent.adapter
            layoutManager = parent.layoutManager
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        init(parent)
        if (layoutManager is GridLayoutManager) {
            drawHorizontal(c, parent)
            drawVertical(c, parent)
        } else if (layoutManager is LinearLayoutManager) {
            val lLayoutManager = layoutManager as LinearLayoutManager
            val orientation = lLayoutManager.orientation
            if (orientation == LinearLayoutManager.VERTICAL)
                drawHorizontal(c, parent)
            else
                drawVertical(c, parent)
        }
    }

    //绘制横向 item 分割线
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        var right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerHeight
            val viewType = adapter?.getItemViewType(params.viewAdapterPosition) ?: 0
            if (viewType == BaseRecyclerAdapter.TYPE_ITEM) {
                if (layoutManager is GridLayoutManager) {
                    right = child.right + params.rightMargin
                }
                if (drawable != null) {
                    drawable.setBounds(left, top, right, bottom)
                    drawable.draw(canvas)
                } else if (mPaint != null)
                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
            }
        }
    }

    //绘制纵向 item 分割线
    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        var bottom = parent.measuredHeight - parent.paddingBottom
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + dividerHeight
            val viewType = adapter?.getItemViewType(params.viewAdapterPosition) ?: 0
            if (viewType == BaseRecyclerAdapter.TYPE_ITEM) {
                if (layoutManager is GridLayoutManager) {
                    bottom = child.bottom + dividerHeight + params.bottomMargin
                }
                if (drawable != null) {
                    drawable.setBounds(left, top, right, bottom)
                    drawable.draw(canvas)
                } else if (mPaint != null)
                    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        init(parent)
        val params = view.layoutParams as RecyclerView.LayoutParams
        var viewAdapterPosition = params.viewAdapterPosition
        val viewType = adapter?.getItemViewType(viewAdapterPosition) ?: 0
        if (viewType == BaseRecyclerAdapter.TYPE_ITEM) {
            if (layoutManager is GridLayoutManager) {
                outRect.set(0, 0, dividerVerticalWidth, dividerHeight)
            } else if (layoutManager is LinearLayoutManager) {
                val lLayoutManager = layoutManager as LinearLayoutManager
                val orientation = lLayoutManager.orientation
                if (orientation == LinearLayoutManager.VERTICAL)
                    outRect.set(0, 0, 0, dividerHeight)
                else
                    outRect.set(0, 0, dividerHeight, 0)
            }
        } else
            outRect.set(0, 0, 0, 0)
    }
}
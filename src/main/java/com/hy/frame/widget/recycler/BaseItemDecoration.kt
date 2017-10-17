package com.hy.frame.widget.recycler

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hy.frame.widget.recycler.BaseRecyclerAdapter

/**
 * BaseItemDecoration
 * @author HeYan
 * @time 2017/10/13 10:11
 */
abstract class BaseItemDecoration : RecyclerView.ItemDecoration {
    protected var divider: Int = 0
    protected var dividerVertical: Int = 0
    protected var drawable: Drawable? = null
    protected var mPaint: Paint? = null
    protected var paddingTop: Int = 0
    protected var paddingLeft: Int = 0
    protected var paddingRight: Int = 0
    protected var paddingBottom: Int = 0
    protected val rcyList: RecyclerView
    protected var adapter: BaseRecyclerAdapter<*, *>? = null

    constructor(rcyList: RecyclerView, divider: Int, color: Int) {
        this.rcyList = rcyList
        this.divider = divider
        this.drawable = null
        this.mPaint = Paint()
        this.mPaint?.color = color
        this.mPaint?.flags = Paint.ANTI_ALIAS_FLAG
    }

    constructor(rcyList: RecyclerView, divider: Int, drawable: Drawable) {
        this.rcyList = rcyList
        this.divider = divider
        this.drawable = drawable
        this.mPaint = null
    }

    fun setDividerVertical(dividerVertical: Int): BaseItemDecoration {
        this.dividerVertical = dividerVertical
        return this
    }

    fun setPaddingTop(paddingTop: Int = 0): BaseItemDecoration {
        this.paddingTop = paddingTop
        return this
    }

    fun setPaddingLeft(paddingLeft: Int = 0): BaseItemDecoration {
        this.paddingLeft = paddingLeft
        return this
    }

    fun setPaddingRight(paddingRight: Int = 0): BaseItemDecoration {
        this.paddingRight = paddingRight
        return this
    }


    fun setPaddingBottom(paddingBottom: Int = 0): BaseItemDecoration {
        this.paddingBottom = paddingBottom
        return this
    }

    open fun build(): BaseItemDecoration {
        return this
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mPaint != null && mPaint!!.alpha == 0 || drawable != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && drawable!!.alpha == 0) return
        drawDivider(c)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (adapter == null) {
            if (parent.adapter == null || parent.adapter !is BaseRecyclerAdapter<*, *>) throw RuntimeException("adapter is not BaseRecyclerAdapter")
            adapter = parent.adapter as BaseRecyclerAdapter<*, *>
            //initData()
        }
        configureItemOutRect(outRect, view)
    }

    protected abstract fun drawDivider(canvas: Canvas)
    protected abstract fun configureItemOutRect(outRect: Rect, view: View)
}
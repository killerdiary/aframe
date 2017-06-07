/*
 * Copyright 2015 Bartosz Lipinski
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hy.frame.widget.recycler

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.support.annotation.CallSuper
import android.support.annotation.IntDef
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class RecyclerViewHeader : RelativeLayout {

    @Visibility
    private var intendedVisibility = View.VISIBLE
    private var downTranslation: Int = 0
    private var hidden = false
    private var recyclerWantsTouch: Boolean = false
    private var isVertical: Boolean = false
    private var isAttachedToRecycler: Boolean = false
    private var recyclerView: RecyclerViewDelegate? = null
    private var layoutManager: LayoutManagerDelegate? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    /**
     * Attaches `RecyclerViewHeader` to `RecyclerView`.
     * Be sure that `setLayoutManager(...)` has been called for `RecyclerView` before calling this method.

     * @param recycler `RecyclerView` to attach `RecyclerViewHeader` to.
     */
    fun attachTo(recycler: RecyclerView) {
        validate(recycler)
        this.recyclerView = RecyclerViewDelegate.with(recycler)
        this.layoutManager = LayoutManagerDelegate.with(recycler.layoutManager)
        isVertical = layoutManager!!.isVertical
        isAttachedToRecycler = true
        recyclerView!!.setHeaderDecoration(HeaderItemDecoration())
        recyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                onScrollChanged()
            }
        })
        recyclerView!!.setOnChildAttachListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}

            override fun onChildViewDetachedFromWindow(view: View) {
                recycler.post {
                    recyclerView!!.invalidateItemDecorations()
                    onScrollChanged()
                }
            }
        })
    }

    /**
     * Detaches `RecyclerViewHeader` from `RecyclerView`.
     */
    fun detach() {
        if (isAttachedToRecycler) {
            isAttachedToRecycler = false
            recyclerWantsTouch = false
            recyclerView!!.reset()
            recyclerView = null
            layoutManager = null
        }
    }

    private fun onScrollChanged() {
        hidden = recyclerView!!.hasItems() && !layoutManager!!.isFirstRowVisible
        super@RecyclerViewHeader.setVisibility(if (hidden) View.INVISIBLE else intendedVisibility)
        if (!hidden) {
            val translation = calculateTranslation()
            if (isVertical) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    translationY = translation.toFloat()
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    translationX = translation.toFloat()
                }
            }
        }
    }

    fun calculateTranslation(): Int {
        if (recyclerView == null || layoutManager == null) return 0
        val offset = recyclerView!!.getScrollOffset(isVertical)
        val base = if (layoutManager!!.isReversed) recyclerView!!.getTranslationBase(isVertical) else 0
        return base - offset
    }

    override fun setVisibility(@Visibility visibility: Int) {
        this.intendedVisibility = visibility
        if (!hidden) {
            super.setVisibility(intendedVisibility)
        }
    }

    @Visibility
    override fun getVisibility(): Int {
        return intendedVisibility
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed && isAttachedToRecycler) {
            var verticalMargins = 0
            var horizontalMargins = 0
            if (layoutParams is MarginLayoutParams) {
                val layoutParams = layoutParams as MarginLayoutParams
                verticalMargins = layoutParams.topMargin + layoutParams.bottomMargin
                horizontalMargins = layoutParams.leftMargin + layoutParams.rightMargin
            }
            recyclerView!!.onHeaderSizeChanged(height + verticalMargins, width + horizontalMargins)
            onScrollChanged()
        }
    }

    @CallSuper
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        recyclerWantsTouch = isAttachedToRecycler && recyclerView!!.onInterceptTouchEvent(ev)
        if (recyclerWantsTouch && ev.action == MotionEvent.ACTION_MOVE) {
            downTranslation = calculateTranslation()
        }
        val superEv = super.onInterceptTouchEvent(ev)//让父类获得正确的参数，避免异常
        return recyclerWantsTouch || superEv
    }

    @CallSuper
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (recyclerWantsTouch) { // this cannot be true if recycler is not attached
            val scrollDiff = downTranslation - calculateTranslation()
            val verticalDiff = if (isVertical) scrollDiff else 0
            val horizontalDiff = if (isVertical) 0 else scrollDiff
            val recyclerEvent = MotionEvent.obtain(event.downTime,
                    event.eventTime,
                    event.action,
                    event.x - horizontalDiff,
                    event.y - verticalDiff,
                    event.metaState)
            try {
                recyclerView!!.onTouchEvent(recyclerEvent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }
        return super.onTouchEvent(event)
    }

    private fun validate(recyclerView: RecyclerView) {
        if (recyclerView.layoutManager == null) {
            throw IllegalStateException("Be sure to attach RecyclerViewHeader after setting your RecyclerView's LayoutManager.")
        }
    }

    private inner class HeaderItemDecoration : RecyclerView.ItemDecoration() {
        private var headerHeight: Int = 0
        private var headerWidth: Int = 0
        private val firstRowSpan: Int

        init {
            firstRowSpan = layoutManager!!.firstRowSpan
        }

        fun setWidth(width: Int) {
            headerWidth = width
        }

        fun setHeight(height: Int) {
            headerHeight = height
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
            super.getItemOffsets(outRect, view, parent, state)
            val headerRelatedPosition = parent.getChildLayoutPosition(view) < firstRowSpan
            val heightOffset = if (headerRelatedPosition && isVertical) headerHeight else 0
            val widthOffset = if (headerRelatedPosition && !isVertical) headerWidth else 0
            if (layoutManager!!.isReversed) {
                outRect.bottom = heightOffset
                outRect.right = widthOffset
            } else {
                outRect.top = heightOffset
                outRect.left = widthOffset
            }
        }
    }

    private class RecyclerViewDelegate private constructor(private val recyclerView: RecyclerView) {
        private var decoration: HeaderItemDecoration? = null
        private var onScrollListener: RecyclerView.OnScrollListener? = null
        private var onChildAttachListener: RecyclerView.OnChildAttachStateChangeListener? = null

        fun onHeaderSizeChanged(height: Int, width: Int) {
            if (decoration != null) {
                decoration!!.setHeight(height)
                decoration!!.setWidth(width)
                recyclerView.post { invalidateItemDecorations() }
            }
        }

        internal fun invalidateItemDecorations() {
            if (!recyclerView.isComputingLayout) {
                recyclerView.invalidateItemDecorations()
            }
        }

        fun getScrollOffset(isVertical: Boolean): Int {
            return if (isVertical) recyclerView.computeVerticalScrollOffset() else recyclerView.computeHorizontalScrollOffset()
        }

        fun getTranslationBase(isVertical: Boolean): Int {
            return if (isVertical)
                recyclerView.computeVerticalScrollRange() - recyclerView.height
            else
                recyclerView.computeHorizontalScrollRange() - recyclerView.width
        }

        fun hasItems(): Boolean {
            return recyclerView.adapter != null && recyclerView.adapter.itemCount != 0
        }

        fun setHeaderDecoration(decoration: HeaderItemDecoration) {
            clearHeaderDecoration()
            this.decoration = decoration
            recyclerView.addItemDecoration(this.decoration, 0)
        }

        fun clearHeaderDecoration() {
            if (decoration != null) {
                recyclerView.removeItemDecoration(decoration)
                decoration = null
            }
        }

        fun setOnScrollListener(onScrollListener: RecyclerView.OnScrollListener) {
            clearOnScrollListener()
            this.onScrollListener = onScrollListener
            recyclerView.addOnScrollListener(this.onScrollListener)
        }

        fun clearOnScrollListener() {
            if (onScrollListener != null) {
                recyclerView.removeOnScrollListener(onScrollListener)
                onScrollListener = null
            }
        }

        fun setOnChildAttachListener(onChildAttachListener: RecyclerView.OnChildAttachStateChangeListener) {
            clearOnChildAttachListener()
            this.onChildAttachListener = onChildAttachListener
            recyclerView.addOnChildAttachStateChangeListener(this.onChildAttachListener)
        }

        fun clearOnChildAttachListener() {
            if (onChildAttachListener != null) {
                recyclerView.removeOnChildAttachStateChangeListener(onChildAttachListener)
                onChildAttachListener = null
            }
        }

        fun reset() {
            clearHeaderDecoration()
            clearOnScrollListener()
            clearOnChildAttachListener()
        }

        fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
            return recyclerView.onInterceptTouchEvent(ev)
        }

        fun onTouchEvent(ev: MotionEvent): Boolean {
            return recyclerView.onTouchEvent(ev)
        }

        companion object {

            fun with(recyclerView: RecyclerView): RecyclerViewDelegate {
                return RecyclerViewDelegate(recyclerView)
            }
        }

    }

    private class LayoutManagerDelegate private constructor(manager: RecyclerView.LayoutManager) {
        private val linear: LinearLayoutManager?
        private val grid: GridLayoutManager?
        private val staggeredGrid: StaggeredGridLayoutManager?

        init {
            val managerClass = manager.javaClass
            if (managerClass == LinearLayoutManager::class.java) { //not using instanceof on purpose
                linear = manager as LinearLayoutManager
                grid = null
                staggeredGrid = null
            } else if (managerClass == GridLayoutManager::class.java) {
                linear = null
                grid = manager as GridLayoutManager
                staggeredGrid = null
                //            } else if (manager instanceof StaggeredGridLayoutManager) { //TODO: 05.04.2016 implement staggered
                //                linear = null;
                //                grid = null;
                //                staggeredGrid = (StaggeredGridLayoutManager) manager;
            } else {
                throw IllegalArgumentException("Currently RecyclerViewHeader supports only LinearLayoutManager and GridLayoutManager.")
            }
        }

        //            } else if (staggeredGrid != null) {
        //                return staggeredGrid.getSpanCount(); //TODO: 05.04.2016 implement staggered
        //shouldn't get here
        val firstRowSpan: Int
            get() {
                if (linear != null) {
                    return 1
                } else if (grid != null) {
                    return grid.spanCount
                }
                return 0
            }

        //            } else if (staggeredGrid != null) {
        //                return staggeredGrid.findFirstCompletelyVisibleItemPositions() //TODO: 05.04.2016 implement staggered
        //shouldn't get here
        val isFirstRowVisible: Boolean
            get() {
                if (linear != null) {
                    return linear.findFirstVisibleItemPosition() == 0
                } else if (grid != null) {
                    return grid.findFirstVisibleItemPosition() == 0
                }
                return false
            }

        //            } else if (staggeredGrid != null) {
        //                return ; //TODO: 05.04.2016 implement staggered
        //shouldn't get here
        val isReversed: Boolean
            get() {
                if (linear != null) {
                    return linear.reverseLayout
                } else if (grid != null) {
                    return grid.reverseLayout
                }
                return false
            }

        //            } else if (staggeredGrid != null) {
        //                return ; //TODO: 05.04.2016 implement staggered
        //shouldn't get here
        val isVertical: Boolean
            get() {
                if (linear != null) {
                    return linear.orientation == LinearLayoutManager.VERTICAL
                } else if (grid != null) {
                    return grid.orientation == LinearLayoutManager.VERTICAL
                }
                return false
            }

        companion object {

            fun with(layoutManager: RecyclerView.LayoutManager): LayoutManagerDelegate {
                return LayoutManagerDelegate(layoutManager)
            }
        }
    }

    @IntDef(View.VISIBLE.toLong(), View.INVISIBLE.toLong(), View.GONE.toLong())
    @Retention(RetentionPolicy.SOURCE)
    private annotation class Visibility

}
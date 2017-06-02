package com.hy.frame.widget.recycler

import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.hy.frame.R
import com.hy.frame.util.MyLog
import java.util.*

/**
 * RecyclerViewPager

 * @author Green
 */
open class RecyclerViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    private var mViewPagerAdapter: RecyclerViewPagerAdapter<*>? = null
    var triggerOffset = 0.25f
    var flingFactor = 0.15f
    private var mTouchSpan: Float = 0.toFloat()
    private var mOnPageChangedListeners: MutableList<OnPageChangedListener>? = null
    private var mSmoothScrollTargetPosition = -1
    private var mPositionBeforeScroll = -1

    var isSinglePageFling: Boolean = false

    internal var mNeedAdjust: Boolean = false
    internal var mFisrtLeftWhenDragging: Int = 0
    internal var mFirstTopWhenDragging: Int = 0
    internal var mCurView: View? = null
    internal var mMaxLeftWhenDragging = Integer.MIN_VALUE
    internal var mMinLeftWhenDragging = Integer.MAX_VALUE
    internal var mMaxTopWhenDragging = Integer.MIN_VALUE
    internal var mMinTopWhenDragging = Integer.MAX_VALUE
    private var mPositionOnTouchDown = -1
    private var mHasCalledOnPageChanged = true
    private var reverseLayout = false

    init {
        //DEBUG = MyLog.isLoggable;
        initAttrs(context, attrs, defStyle)
        isNestedScrollingEnabled = false
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewPager, defStyle,
                0)
        flingFactor = a.getFloat(R.styleable.RecyclerViewPager_rvp_flingFactor, 0.15f)
        triggerOffset = a.getFloat(R.styleable.RecyclerViewPager_rvp_triggerOffset, 0.25f)
        isSinglePageFling = a.getBoolean(R.styleable.RecyclerViewPager_rvp_singlePageFling, isSinglePageFling)
        a.recycle()
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        try {
            val fLayoutState = state.javaClass.getDeclaredField("mLayoutState")
            fLayoutState.isAccessible = true
            val layoutState = fLayoutState.get(state)
            val fAnchorOffset = layoutState.javaClass.getDeclaredField("mAnchorOffset")
            val fAnchorPosition = layoutState.javaClass.getDeclaredField("mAnchorPosition")
            fAnchorPosition.isAccessible = true
            fAnchorOffset.isAccessible = true
            if (fAnchorOffset.getInt(layoutState) > 0) {
                fAnchorPosition.set(layoutState, fAnchorPosition.getInt(layoutState) - 1)
            } else if (fAnchorOffset.getInt(layoutState) < 0) {
                fAnchorPosition.set(layoutState, fAnchorPosition.getInt(layoutState) + 1)
            }
            fAnchorOffset.setInt(layoutState, 0)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        super.onRestoreInstanceState(state)
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mViewPagerAdapter = ensureRecyclerViewPagerAdapter(adapter)
        super.setAdapter(mViewPagerAdapter)
    }

    override fun swapAdapter(adapter: RecyclerView.Adapter<*>, removeAndRecycleExistingViews: Boolean) {
        mViewPagerAdapter = ensureRecyclerViewPagerAdapter(adapter)
        super.swapAdapter(mViewPagerAdapter, removeAndRecycleExistingViews)
    }

    override fun getAdapter(): RecyclerView.Adapter<*>? {
        if (mViewPagerAdapter != null) {
            return mViewPagerAdapter!!.mAdapter
        }
        return null
    }

    val wrapperAdapter: RecyclerViewPagerAdapter<*>?
        get() = mViewPagerAdapter

    override fun setLayoutManager(layout: RecyclerView.LayoutManager) {
        super.setLayoutManager(layout)

        if (layout is LinearLayoutManager) {
            reverseLayout = layout.reverseLayout
        }
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val flinging = super.fling((velocityX * flingFactor).toInt(), (velocityY * flingFactor).toInt())
        if (flinging) {
            if (layoutManager.canScrollHorizontally()) {
                adjustPositionX(velocityX)
            } else {
                adjustPositionY(velocityY)
            }
        }

        if (DEBUG) {
            MyLog.d("@", "velocityX:" + velocityX)
            MyLog.d("@", "velocityY:" + velocityY)
        }
        return flinging
    }

    override fun smoothScrollToPosition(position: Int) {
        if (DEBUG) {
            MyLog.d("@", "smoothScrollToPosition:" + position)
        }
        mSmoothScrollTargetPosition = position
        if (layoutManager != null && layoutManager is LinearLayoutManager) {
            // exclude item decoration
            val linearSmoothScroller = object : LinearSmoothScroller(context) {
                override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                    if (layoutManager == null) {
                        return null
                    }
                    return (layoutManager as LinearLayoutManager)
                            .computeScrollVectorForPosition(targetPosition)
                }

                override fun onTargetFound(targetView: View, state: RecyclerView.State?, action: RecyclerView.SmoothScroller.Action) {
                    if (layoutManager == null) {
                        return
                    }
                    var dx = calculateDxToMakeVisible(targetView,
                            horizontalSnapPreference)
                    var dy = calculateDyToMakeVisible(targetView,
                            verticalSnapPreference)
                    if (dx > 0) {
                        dx = dx - layoutManager!!
                                .getLeftDecorationWidth(targetView)
                    } else {
                        dx = dx + layoutManager!!
                                .getRightDecorationWidth(targetView)
                    }
                    if (dy > 0) {
                        dy = dy - layoutManager!!
                                .getTopDecorationHeight(targetView)
                    } else {
                        dy = dy + layoutManager!!
                                .getBottomDecorationHeight(targetView)
                    }
                    val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toInt()
                    val time = calculateTimeForDeceleration(distance)
                    if (time > 0) {
                        action.update(-dx, -dy, time, mDecelerateInterpolator)
                    }
                }
            }
            linearSmoothScroller.targetPosition = position
            if (position == RecyclerView.NO_POSITION) {
                return
            }
            layoutManager.startSmoothScroll(linearSmoothScroller)
        } else {
            super.smoothScrollToPosition(position)
        }
    }

    override fun scrollToPosition(position: Int) {
        if (DEBUG) {
            MyLog.d("@", "scrollToPosition:" + position)
        }
        mPositionBeforeScroll = currentPosition
        mSmoothScrollTargetPosition = position
        super.scrollToPosition(position)

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

                if (mSmoothScrollTargetPosition >= 0 && mSmoothScrollTargetPosition < mViewPagerAdapter!!.itemCount) {
                    if (mOnPageChangedListeners != null) {
                        for (onPageChangedListener in mOnPageChangedListeners!!) {
                            onPageChangedListener?.OnPageChanged(mPositionBeforeScroll, currentPosition)
                        }
                    }
                }
            }
        })
    }

    /**
     * get item position in center of viewpager
     */
    val currentPosition: Int
        get() {
            var curPosition = -1
            if (layoutManager.canScrollHorizontally()) {
                curPosition = ViewUtils.getCenterXChildPosition(this)
            } else {
                curPosition = ViewUtils.getCenterYChildPosition(this)
            }
            if (curPosition < 0) {
                curPosition = mSmoothScrollTargetPosition
            }
            return curPosition
        }

    /***
     * adjust position before Touch event complete and fling action start.
     */
    protected fun adjustPositionX(velocityX: Int) {
        var velocityX = velocityX
        if (reverseLayout) velocityX *= -1

        val childCount = childCount
        if (childCount > 0) {
            val curPosition = ViewUtils.getCenterXChildPosition(this)
            val childWidth = width - paddingLeft - paddingRight
            var flingCount = getFlingCount(velocityX, childWidth)
            var targetPosition = curPosition + flingCount
            if (isSinglePageFling) {
                flingCount = Math.max(-1, Math.min(1, flingCount))
                targetPosition = if (flingCount == 0) curPosition else mPositionOnTouchDown + flingCount
                if (DEBUG) {
                    MyLog.d("@", "flingCount:" + flingCount)
                    MyLog.d("@", "original targetPosition:" + targetPosition)
                }
            }
            targetPosition = Math.max(targetPosition, 0)
            targetPosition = Math.min(targetPosition, mViewPagerAdapter!!.itemCount - 1)
            if (targetPosition == curPosition && (isSinglePageFling && mPositionOnTouchDown == curPosition || !isSinglePageFling)) {
                val centerXChild = ViewUtils.getCenterXChild(this)
                if (centerXChild != null) {
                    if (mTouchSpan > centerXChild.width.toFloat() * triggerOffset * triggerOffset && targetPosition != 0) {
                        if (!reverseLayout)
                            targetPosition--
                        else
                            targetPosition++
                    } else if (mTouchSpan < centerXChild.width * -triggerOffset && targetPosition != mViewPagerAdapter!!.itemCount - 1) {
                        if (!reverseLayout)
                            targetPosition++
                        else
                            targetPosition--
                    }
                }
            }
            if (DEBUG) {
                MyLog.d("@", "mTouchSpan:" + mTouchSpan)
                MyLog.d("@", "adjustPositionX:" + targetPosition)
            }
            smoothScrollToPosition(safeTargetPosition(targetPosition, mViewPagerAdapter!!.itemCount))
        }
    }

    fun addOnPageChangedListener(listener: OnPageChangedListener) {
        if (mOnPageChangedListeners == null) {
            mOnPageChangedListeners = ArrayList<OnPageChangedListener>()
        }
        mOnPageChangedListeners!!.add(listener)
    }

    fun removeOnPageChangedListener(listener: OnPageChangedListener) {
        if (mOnPageChangedListeners != null) {
            mOnPageChangedListeners!!.remove(listener)
        }
    }

    fun clearOnPageChangedListeners() {
        if (mOnPageChangedListeners != null) {
            mOnPageChangedListeners!!.clear()
        }
    }

    /***
     * adjust position before Touch event complete and fling action start.
     */
    protected fun adjustPositionY(velocityY: Int) {
        var velocityY = velocityY
        if (reverseLayout) velocityY *= -1

        val childCount = childCount
        if (childCount > 0) {
            val curPosition = ViewUtils.getCenterYChildPosition(this)
            val childHeight = height - paddingTop - paddingBottom
            var flingCount = getFlingCount(velocityY, childHeight)
            var targetPosition = curPosition + flingCount
            if (isSinglePageFling) {
                flingCount = Math.max(-1, Math.min(1, flingCount))
                targetPosition = if (flingCount == 0) curPosition else mPositionOnTouchDown + flingCount
            }

            targetPosition = Math.max(targetPosition, 0)
            targetPosition = Math.min(targetPosition, mViewPagerAdapter!!.itemCount - 1)
            if (targetPosition == curPosition && (isSinglePageFling && mPositionOnTouchDown == curPosition || !isSinglePageFling)) {
                val centerYChild = ViewUtils.getCenterYChild(this)
                if (centerYChild != null) {
                    if (mTouchSpan > centerYChild.height * triggerOffset && targetPosition != 0) {
                        if (!reverseLayout)
                            targetPosition--
                        else
                            targetPosition++
                    } else if (mTouchSpan < centerYChild.height * -triggerOffset && targetPosition != mViewPagerAdapter!!.itemCount - 1) {
                        if (!reverseLayout)
                            targetPosition++
                        else
                            targetPosition--
                    }
                }
            }
            if (DEBUG) {
                MyLog.d("@", "mTouchSpan:" + mTouchSpan)
                MyLog.d("@", "adjustPositionY:" + targetPosition)
            }
            smoothScrollToPosition(safeTargetPosition(targetPosition, mViewPagerAdapter!!.itemCount))
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && layoutManager != null) {
            mPositionOnTouchDown = if (layoutManager.canScrollHorizontally())
                ViewUtils.getCenterXChildPosition(this)
            else
                ViewUtils.getCenterYChildPosition(this)
            if (DEBUG) {
                MyLog.d("@", "mPositionOnTouchDown:" + mPositionOnTouchDown)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // recording the max/min value in touch track
        if (e.action == MotionEvent.ACTION_MOVE) {
            if (mCurView != null) {
                mMaxLeftWhenDragging = Math.max(mCurView!!.left, mMaxLeftWhenDragging)
                mMaxTopWhenDragging = Math.max(mCurView!!.top, mMaxTopWhenDragging)
                mMinLeftWhenDragging = Math.min(mCurView!!.left, mMinLeftWhenDragging)
                mMinTopWhenDragging = Math.min(mCurView!!.top, mMinTopWhenDragging)
            }
        }
        return super.onTouchEvent(e)
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
            mNeedAdjust = true
            mCurView = if (layoutManager.canScrollHorizontally())
                ViewUtils.getCenterXChild(this)
            else
                ViewUtils.getCenterYChild(this)
            if (mCurView != null) {
                if (mHasCalledOnPageChanged) {
                    // While rvp is scrolling, mPositionBeforeScroll will be previous value.
                    mPositionBeforeScroll = getChildLayoutPosition(mCurView)
                    mHasCalledOnPageChanged = false
                }
                if (DEBUG) {
                    MyLog.d("@", "mPositionBeforeScroll:" + mPositionBeforeScroll)
                }
                mFisrtLeftWhenDragging = mCurView!!.left
                mFirstTopWhenDragging = mCurView!!.top
            } else {
                mPositionBeforeScroll = -1
            }
            mTouchSpan = 0f
        } else if (state == RecyclerView.SCROLL_STATE_SETTLING) {
            mNeedAdjust = false
            if (mCurView != null) {
                if (layoutManager.canScrollHorizontally()) {
                    mTouchSpan = (mCurView!!.left - mFisrtLeftWhenDragging).toFloat()
                } else {
                    mTouchSpan = (mCurView!!.top - mFirstTopWhenDragging).toFloat()
                }
            } else {
                mTouchSpan = 0f
            }
            mCurView = null
        } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
            if (mNeedAdjust) {
                var targetPosition = if (layoutManager.canScrollHorizontally())
                    ViewUtils.getCenterXChildPosition(this)
                else
                    ViewUtils.getCenterYChildPosition(this)
                if (mCurView != null) {
                    targetPosition = getChildAdapterPosition(mCurView)
                    if (layoutManager.canScrollHorizontally()) {
                        val spanX = mCurView!!.left - mFisrtLeftWhenDragging
                        // if user is tending to cancel paging action, don't perform position changing
                        if (spanX > mCurView!!.width * triggerOffset && mCurView!!.left >= mMaxLeftWhenDragging) {
                            if (!reverseLayout)
                                targetPosition--
                            else
                                targetPosition++
                        } else if (spanX < mCurView!!.width * -triggerOffset && mCurView!!.left <= mMinLeftWhenDragging) {
                            if (!reverseLayout)
                                targetPosition++
                            else
                                targetPosition--
                        }
                    } else {
                        val spanY = mCurView!!.top - mFirstTopWhenDragging
                        if (spanY > mCurView!!.height * triggerOffset && mCurView!!.top >= mMaxTopWhenDragging) {
                            if (!reverseLayout)
                                targetPosition--
                            else
                                targetPosition++
                        } else if (spanY < mCurView!!.height * -triggerOffset && mCurView!!.top <= mMinTopWhenDragging) {
                            if (!reverseLayout)
                                targetPosition++
                            else
                                targetPosition--
                        }
                    }
                }
                if (mViewPagerAdapter != null)
                    smoothScrollToPosition(safeTargetPosition(targetPosition, mViewPagerAdapter!!.itemCount))
                mCurView = null
            } else if (mSmoothScrollTargetPosition != mPositionBeforeScroll) {
                if (DEBUG) {
                    MyLog.d("@", "onPageChanged:" + mSmoothScrollTargetPosition)
                }
                if (mOnPageChangedListeners != null) {
                    for (onPageChangedListener in mOnPageChangedListeners!!) {
                        onPageChangedListener?.OnPageChanged(mPositionBeforeScroll, mSmoothScrollTargetPosition)
                    }
                }
                mHasCalledOnPageChanged = true
                mPositionBeforeScroll = mSmoothScrollTargetPosition
            }
            // reset
            mMaxLeftWhenDragging = Integer.MIN_VALUE
            mMinLeftWhenDragging = Integer.MAX_VALUE
            mMaxTopWhenDragging = Integer.MIN_VALUE
            mMinTopWhenDragging = Integer.MAX_VALUE
        }
    }

    protected open fun ensureRecyclerViewPagerAdapter(adapter: RecyclerView.Adapter<*>): RecyclerViewPagerAdapter<*> {
        return adapter as? RecyclerViewPagerAdapter<*> ?: RecyclerViewPagerAdapter(this, adapter)

    }

    private fun getFlingCount(velocity: Int, cellSize: Int): Int {
        if (velocity == 0) {
            return 0
        }
        val sign = if (velocity > 0) 1 else -1
        return (sign * Math.ceil((velocity.toFloat() * sign.toFloat() * flingFactor / cellSize - triggerOffset).toDouble())).toInt()
    }

    private fun safeTargetPosition(position: Int, count: Int): Int {
        if (position < 0) {
            return 0
        }
        if (position >= count) {
            return count - 1
        }
        return position
    }

    interface OnPageChangedListener {
        fun OnPageChanged(oldPosition: Int, newPosition: Int)
    }

    companion object {
        var DEBUG = false
    }

}

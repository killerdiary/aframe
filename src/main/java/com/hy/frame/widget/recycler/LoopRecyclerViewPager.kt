package com.hy.frame.widget.recycler

import android.content.Context
import android.util.AttributeSet
import android.util.Log

class LoopRecyclerViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerViewPager(context, attrs, defStyle) {

    override fun setAdapter(adapter: Adapter<*>) {
        super.setAdapter(adapter)
        super.scrollToPosition(middlePosition)
    }

    override fun swapAdapter(adapter: Adapter<*>, removeAndRecycleExistingViews: Boolean) {
        super.swapAdapter(adapter, removeAndRecycleExistingViews)
        super.scrollToPosition(middlePosition)
    }

    override fun ensureRecyclerViewPagerAdapter(adapter: Adapter<*>): RecyclerViewPagerAdapter<*> {
        return adapter as? LoopRecyclerViewPagerAdapter<*> ?: LoopRecyclerViewPagerAdapter(this, adapter)
    }

    /**
     * Starts a smooth scroll to an adapter position.
     * if position < adapter.getActualCount,
     * position will be transform to right position.

     * @param position target position
     */
    override fun smoothScrollToPosition(position: Int) {
        val transformedPosition = transformInnerPositionIfNeed(position)
        super.smoothScrollToPosition(transformedPosition)
        Log.e("test", "transformedPosition:" + transformedPosition)
    }

    /**
     * Starts a scroll to an adapter position.
     * if position < adapter.getActualCount,
     * position will be transform to right position.

     * @param position target position
     */
    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(transformInnerPositionIfNeed(position))
    }

    /**
     * get actual current position in actual adapter.
     */
    val actualCurrentPosition: Int
        get() {
            val position = currentPosition
            return transformToActualPosition(position)
        }

    /**
     * Transform adapter position to actual position.
     * @param position adapter position
     * *
     * @return actual position
     */
    fun transformToActualPosition(position: Int): Int {
        return position % actualItemCountFromAdapter
    }

    private val actualItemCountFromAdapter: Int
        get() = (wrapperAdapter as LoopRecyclerViewPagerAdapter<*>).actualItemCount

    private fun transformInnerPositionIfNeed(position: Int): Int {
        val actualItemCount = actualItemCountFromAdapter
        val actualCurrentPosition = currentPosition % actualItemCount
        val bakPosition1 = currentPosition - actualCurrentPosition + position % actualItemCount
        val bakPosition2 = currentPosition
        -actualCurrentPosition
        -actualItemCount + position % actualItemCount
        val bakPosition3 = currentPosition - actualCurrentPosition
        +actualItemCount
        +position % actualItemCount
        Log.e("test", bakPosition1.toString() + "/" + bakPosition2 + "/" + bakPosition3 + "/" + currentPosition)
        // get position which is closer to current position
        if (Math.abs(bakPosition1 - currentPosition) > Math.abs(bakPosition2 - currentPosition)) {
            if (Math.abs(bakPosition2 - currentPosition) > Math.abs(bakPosition3 - currentPosition)) {
                return bakPosition3
            }
            return bakPosition2
        } else {
            if (Math.abs(bakPosition1 - currentPosition) > Math.abs(bakPosition3 - currentPosition)) {
                return bakPosition3
            }
            return bakPosition1
        }
    }

    private val middlePosition: Int
        get() {
            var middlePosition = Integer.MAX_VALUE / 2
            val actualItemCount = actualItemCountFromAdapter
            if (actualItemCount > 0 && middlePosition % actualItemCount != 0) {
                middlePosition = middlePosition - middlePosition % actualItemCount
            }
            return middlePosition
        }
}

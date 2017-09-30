package com.hy.frame.widget.recycler

import android.support.v7.widget.RecyclerView
import android.util.Log

import java.lang.reflect.Field
@Deprecated("")
class LoopRecyclerViewPagerAdapter<VH : RecyclerView.ViewHolder>(viewPager: RecyclerViewPager, adapter: RecyclerView.Adapter<VH>) : RecyclerViewPagerAdapter<VH>(viewPager, adapter) {

    private var mPositionField: Field? = null

    val actualItemCount: Int
        get() = super.getItemCount()

    fun getActualItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    fun getActualItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(getActualPosition(position))
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(getActualPosition(position))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        super.onBindViewHolder(holder, getActualPosition(position))
        // because of getCurrentPosition may return ViewHolder‘s position,
        // so we must reset mPosition if exists.
        if (mPositionField == null) {
            try {
                mPositionField = holder.javaClass.getDeclaredField("mPosition")
                mPositionField!!.isAccessible = true
            } catch (e: NoSuchFieldException) {
                Log.i(TAG, "The holder doesn't have a mPosition field.")
            }

        }
        if (mPositionField != null) {
            try {
                mPositionField!!.set(holder, position)
            } catch (e: Exception) {
                Log.w(TAG, "Error while updating holder's mPosition field", e)
            }

        }
    }

    fun getActualPosition(position: Int): Int {
        var actualPosition = position
        if (position >= actualItemCount) {
            actualPosition = position % actualItemCount
        }
        return actualPosition
    }

    companion object {

        private val TAG = LoopRecyclerViewPager::class.java.simpleName
    }
}

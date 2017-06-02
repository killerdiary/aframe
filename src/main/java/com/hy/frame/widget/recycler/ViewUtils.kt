package com.hy.frame.widget.recycler

import android.support.v7.widget.RecyclerView
import android.view.View

object ViewUtils {

    /**
     * Get center child in X Axes
     */
    fun getCenterXChild(recyclerView: RecyclerView): View? {
        val childCount = recyclerView.childCount
        if (childCount > 0) {
            for (i in 0..childCount - 1) {
                val child = recyclerView.getChildAt(i)
                if (isChildInCenterX(recyclerView, child)) {
                    return child
                }
            }
        }
        return null
    }

    /**
     * Get position of center child in X Axes
     */
    fun getCenterXChildPosition(recyclerView: RecyclerView): Int {
        val childCount = recyclerView.childCount
        if (childCount > 0) {
            for (i in 0..childCount - 1) {
                val child = recyclerView.getChildAt(i)
                if (isChildInCenterX(recyclerView, child)) {
                    return recyclerView.getChildAdapterPosition(child)
                }
            }
        }
        return childCount
    }

    /**
     * Get center child in Y Axes
     */
    fun getCenterYChild(recyclerView: RecyclerView): View? {
        val childCount = recyclerView.childCount
        if (childCount > 0) {
            for (i in 0..childCount - 1) {
                val child = recyclerView.getChildAt(i)
                if (isChildInCenterY(recyclerView, child)) {
                    return child
                }
            }
        }
        return null
    }

    /**
     * Get position of center child in Y Axes
     */
    fun getCenterYChildPosition(recyclerView: RecyclerView): Int {
        val childCount = recyclerView.childCount
        if (childCount > 0) {
            for (i in 0..childCount - 1) {
                val child = recyclerView.getChildAt(i)
                if (isChildInCenterY(recyclerView, child)) {
                    return recyclerView.getChildAdapterPosition(child)
                }
            }
        }
        return childCount
    }

    fun isChildInCenterX(recyclerView: RecyclerView, view: View): Boolean {
        val childCount = recyclerView.childCount
        val lvLocationOnScreen = IntArray(2)
        val vLocationOnScreen = IntArray(2)
        recyclerView.getLocationOnScreen(lvLocationOnScreen)
        val middleX = lvLocationOnScreen[0] + recyclerView.width / 2
        if (childCount > 0) {
            view.getLocationOnScreen(vLocationOnScreen)
            if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.width >= middleX) {
                return true
            }
        }
        return false
    }

    fun isChildInCenterY(recyclerView: RecyclerView, view: View): Boolean {
        val childCount = recyclerView.childCount
        val lvLocationOnScreen = IntArray(2)
        val vLocationOnScreen = IntArray(2)
        recyclerView.getLocationOnScreen(lvLocationOnScreen)
        val middleY = lvLocationOnScreen[1] + recyclerView.height / 2
        if (childCount > 0) {
            view.getLocationOnScreen(vLocationOnScreen)
            if (vLocationOnScreen[1] <= middleY && vLocationOnScreen[1] + view.height >= middleY) {
                return true
            }
        }
        return false
    }
}

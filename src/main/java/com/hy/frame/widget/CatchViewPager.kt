package com.hy.frame.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent



/**
 * BS_Studio
 * @author HeYan
 * @time 2017/7/6 17:32
 */
class CatchViewPager : ViewPager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            //uncomment if you really want to see these errors
            //e.printStackTrace();
            return false
        }

    }
}
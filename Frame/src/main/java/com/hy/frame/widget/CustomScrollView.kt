package com.hy.frame.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * author HeYan
 * time 2016/8/11 10:08
 */
class CustomScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ScrollView(context, attrs, defStyleAttr) {

    private var scrollListener: ScrollListener? = null
    private var touchListener: TouchListener? = null

    fun setScrollListener(scrollListener: ScrollListener) {
        this.scrollListener = scrollListener
    }

    fun setTouchListener(touchListener: TouchListener) {
        this.touchListener = touchListener
    }

    override fun onScrollChanged(x: Int, y: Int, oldxX: Int, oldY: Int) {
        super.onScrollChanged(x, y, oldxX, oldY)
        if (scrollListener != null) {
            scrollListener!!.onScrollChanged(x, y, oldxX, oldY)
        }
        //        if (v != null) {
        //            if (y >= 0 && y <= 255) {
        //                v.setBackgroundColor(Color.argb(y, 255, 180, 0));
        //            }
        //        }
    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (touchListener != null)
            touchListener!!.onTouch(ev)
        //        switch (ev.getAction()) {
        //            case MotionEvent.ACTION_DOWN:
        //                break;
        //            case MotionEvent.ACTION_MOVE:
        //                break;
        //            case MotionEvent.ACTION_UP:
        //                break;
        //            default:
        //                break;
        //        }
        return super.onTouchEvent(ev)
    }

    interface ScrollListener {
        fun onScrollChanged(x: Int, y: Int, oldxX: Int, oldY: Int)
    }

    interface TouchListener {
        fun onTouch(event: MotionEvent)
    }
}

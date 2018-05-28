package com.hy.frame.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

import com.hy.frame.util.MyLog

/**
 * 显示ViewPager功能
 * @author HeYan
 * @time 2014年9月4日 下午2:37:10
 */
class RefreshScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : ScrollView(context, attrs, defStyle) {
    private var startY: Int = 0
    private var listener: RefreshListener? = null
    private val max: Int = 0
    private var isRecord: Boolean = false// 开始记录
    private var scrollHeight: Int = 0

    interface OnPagerChangeListener {
        fun onPagerChange(pager: Int)
    }

    init {
        init()
    }

    private fun init() {
        isVerticalScrollBarEnabled = false
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        // MyLog.e(scrollX + " " + scrollY + " " + clampedX + " " + clampedY);
        if (scrollY >= 0)
            super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        // if (listener != null)
        // listener.onOverScrolled(scrollY);
        // super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (listener != null) {
            MyLog.e("getScrollY 记录当前位置:" + scrollY)
            // if (getScrollY() <= 1) {
            val y = ev.y.toInt()
            when (ev.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> if (!isRecord) {
                    isRecord = true
                    startY = y
                    scrollHeight = 0
                    MyLog.e("ACTION_DOWN 记录当前位置:" + startY)
                    listener!!.onRefreshEvent(MotionEvent.ACTION_DOWN, 0)
                } else {
                    // int scrollHeight = (y - startY) / RATIO;
                    scrollHeight += 10
                    MyLog.e("ACTION_MOVE 记录当前位置:$y $scrollHeight")
                    listener!!.onRefreshEvent(MotionEvent.ACTION_MOVE, scrollHeight)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isRecord = false
                    // int scrollHeight = (y - startY) / RATIO;
                    listener!!.onRefreshEvent(MotionEvent.ACTION_UP, scrollHeight)
                    MyLog.e("ACTION_UP 记录当前位置:" + scrollHeight)
                }
            }
            // } else {
            // isRecord = false;
            // listener.onRefreshEvent(MotionEvent.ACTION_UP, 0);
            // }
        }
        return super.onTouchEvent(ev)
    }

    fun setListener(listener: RefreshListener) {
        this.listener = listener
    }

    /**
     * 刷新监听接口
     */
    interface RefreshListener {
        fun onRefreshEvent(flag: Int, scrollY: Int)
    }

    companion object {
        private val RATIO = 1// 移动的比例
    }
}

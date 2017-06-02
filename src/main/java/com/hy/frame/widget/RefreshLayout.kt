package com.hy.frame.widget

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.AbsListView
import android.widget.AbsListView.OnScrollListener
import android.widget.ListView

import com.hy.frame.R
import com.hy.frame.util.MyLog

/**

 * 上下拉刷新

 * @author HeYan
 * *
 * @time 2015-9-15 上午10:49:43
 */
class RefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs), OnScrollListener {

    /**
     * 滑动到最下面时的上拉操作
     */
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var lvData: ListView? = null

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private var listener: LoadMoreListener? = null

    /**
     * ListView的加载中footer
     */
    private val vFooter: View = LayoutInflater.from(context).inflate(R.layout.in_lv_footer, null, false)

    /**
     * 按下时的y坐标
     */
    private var mYDown: Int = 0
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private var mLastY: Int = 0
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private var isLoading: Boolean = false
    private var isCanLoad: Boolean = false

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 初始化ListView对象
        if (lvData == null) {
            initData()
        }
    }

    private fun initData() {
        val childs = childCount
        if (childs > 0) {
            val childView = getChildAt(0)
            if (childView is ListView) {
                lvData = childView
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                lvData!!.setOnScrollListener(this)
                MyLog.d(View.VIEW_LOG_TAG, "### 找到listview")
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        if (isCanLoad) {
            when (action) {
                MotionEvent.ACTION_DOWN ->
                    // 按下
                    mYDown = event.rawY.toInt()

                MotionEvent.ACTION_MOVE ->
                    // 移动
                    mLastY = event.rawY.toInt()

                MotionEvent.ACTION_UP ->
                    // 抬起
                    if (canLoad()) {
                        loadData()
                    }
                else -> {
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.

     * @return
     */
    private fun canLoad(): Boolean {
        return isBottom && !isLoading && isPullUp
    }

    /**
     * 判断是否到了最底部
     */
    private val isBottom: Boolean
        get() {

            if (lvData != null && lvData!!.adapter != null) {
                return lvData!!.lastVisiblePosition == lvData!!.adapter.count - 1
            }
            return false
        }

    /**
     * 是否是上拉操作

     * @return
     */
    private val isPullUp: Boolean
        get() = mYDown - mLastY >= mTouchSlop

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private fun loadData() {
        if (listener != null) {
            isLoading = true
            // 设置状态
            showLoadMore()
            listener!!.onLoadMore()
        }
    }

    internal var isAdd: Boolean = false

    private fun showLoadMore() {
        if (!isAdd) {
            isAdd = true
            lvData!!.addFooterView(vFooter)
        }
    }

    private fun cancelLoadMore() {
        if (isAdd) {
            isAdd = false
            lvData!!.removeFooterView(vFooter)
            mYDown = 0
            mLastY = 0
        }
    }

    /**
     * @param listener
     */
    fun setOnLoadListener(listener: LoadMoreListener) {
        this.listener = listener
    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {

    }

    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData()
        }
    }

    fun setCanLoadMore(isCanLoad: Boolean) {
        this.isCanLoad = isCanLoad
        if (isCanLoad) {
            showLoadMore()
        } else {
            cancelLoadMore()
        }
    }

    /**
     * 加载更多的监听器

     * @author HeYan
     * *
     * @time 2015-9-15 上午11:05:49
     */
    interface LoadMoreListener {
        fun onLoadMore()
    }
}
/**
 * @param context
 */
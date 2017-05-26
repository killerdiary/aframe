package com.hy.frame.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.hy.frame.R;
import com.hy.frame.util.MyLog;

/**
 * 
 * 上下拉刷新
 * 
 * @author HeYan
 * @time 2015-9-15 上午10:49:43
 */
public class RefreshLayout extends SwipeRefreshLayout implements OnScrollListener {

    /**
     * 滑动到最下面时的上拉操作
     */
    private int mTouchSlop;
    private ListView lvData;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private LoadMoreListener listener;

    /**
     * ListView的加载中footer
     */
    private View vFooter;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading;
    private boolean isCanLoad;

    /**
     * @param context
     */
    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        vFooter = LayoutInflater.from(context).inflate(R.layout.in_lv_footer, null, false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (lvData == null) {
            initData();
        }
    }

    private void initData() {
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                lvData = (ListView) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                lvData.setOnScrollListener(this);
                MyLog.INSTANCE.d(VIEW_LOG_TAG, "### 找到listview");
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if (isCanLoad) {
            switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     * 
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp();
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {

        if (lvData != null && lvData.getAdapter() != null) {
            return lvData.getLastVisiblePosition() == (lvData.getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * 是否是上拉操作
     * 
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (listener != null) {
            isLoading = true;
            // 设置状态
            showLoadMore();
            listener.onLoadMore();
        }
    }

    boolean isAdd;

    private void showLoadMore() {
        if (!isAdd) {
            isAdd = true;
            lvData.addFooterView(vFooter);
        }
    }

    private void cancelLoadMore() {
        if (isAdd) {
            isAdd = false;
            lvData.removeFooterView(vFooter);
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * @param listener
     */
    public void setOnLoadListener(LoadMoreListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
    }

    public void setCanLoadMore(boolean isCanLoad) {
        this.isCanLoad = isCanLoad;
        if (isCanLoad) {
            showLoadMore();
        } else {
            cancelLoadMore();
        }
    }

    /**
     * 加载更多的监听器
     * 
     * @author HeYan
     * @time 2015-9-15 上午11:05:49
     */
    public interface LoadMoreListener {
        public void onLoadMore();
    }
}
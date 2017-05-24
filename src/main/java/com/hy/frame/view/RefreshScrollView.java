package com.hy.frame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.hy.frame.util.MyLog;

/**
 * 显示ViewPager功能
 *
 * @author HeYan
 * @time 2014年9月4日 下午2:37:10
 */
public class RefreshScrollView extends ScrollView {
    private int startY;
    private RefreshListener listener;
    private int max;
    private boolean isRecord;// 开始记录
    private final static int RATIO = 1;// 移动的比例
    private int height;

    public interface OnPagerChangeListener {
        void onPagerChange(int pager);
    }

    public RefreshScrollView(Context context) {
        this(context, null);
    }

    public RefreshScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setVerticalScrollBarEnabled(false);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        // MyLog.e(scrollX + " " + scrollY + " " + clampedX + " " + clampedY);
        if (scrollY >= 0)
            super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        // if (listener != null)
        // listener.onOverScrolled(scrollY);
        // super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (listener != null) {
            MyLog.e("getScrollY 记录当前位置:" + getScrollY());
            // if (getScrollY() <= 1) {
            int y = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if (!isRecord) {
                        isRecord = true;
                        startY = y;
                        height = 0;
                        MyLog.e("ACTION_DOWN 记录当前位置:" + startY);
                        listener.onRefreshEvent(MotionEvent.ACTION_DOWN, 0);
                    } else {
                        // int height = (y - startY) / RATIO;
                        height += 10;
                        MyLog.e("ACTION_MOVE 记录当前位置:" + y + " " + height);
                        listener.onRefreshEvent(MotionEvent.ACTION_MOVE, height);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isRecord = false;
                    // int height = (y - startY) / RATIO;
                    listener.onRefreshEvent(MotionEvent.ACTION_UP, height);
                    MyLog.e("ACTION_UP 记录当前位置:" + height);
                    break;
            }
            // } else {
            // isRecord = false;
            // listener.onRefreshEvent(MotionEvent.ACTION_UP, 0);
            // }
        }
        return super.onTouchEvent(ev);
    }

    public void setListener(RefreshListener listener) {
        this.listener = listener;
    }

    /**
     * 刷新监听接口
     */
    public interface RefreshListener {
        void onRefreshEvent(int flag, int scrollY);
    }
}

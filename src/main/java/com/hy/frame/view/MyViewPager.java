package com.hy.frame.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
    private boolean canScroll = false;

    public MyViewPager(Context context) {
        super(context);
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (isCanScroll()) {
            return super.onInterceptTouchEvent(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isCanScroll()) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

}

package com.hy.frame.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
    private boolean canScroll = false;
    private boolean canLeftScroll = true;
    private boolean canRightScroll = true;

    public MyViewPager(Context context) {
        super(context);
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public void setCanLeftScroll(boolean canLeftScroll) {
        this.canLeftScroll = canLeftScroll;
    }

    public void setCanRightScroll(boolean canRightScroll) {
        this.canRightScroll = canRightScroll;
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        if (isCanScroll()) {
//
//            return super.onInterceptTouchEvent(event);
//        } else {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (isCanScroll()) {
//
//            return super.onTouchEvent(event);
//        } else {
//            return false;
//        }
//    }
    private float beforeX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isCanScroll()) {
            if (!canLeftScroll || !canRightScroll) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下如果‘仅’作为‘上次坐标’，不妥，因为可能存在左滑，motionValue大于0的情况（来回滑，只要停止坐标在按下坐标的右边，左滑仍然能滑过去）
                        beforeX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float motionValue = event.getX() - beforeX;
                        if (!canLeftScroll && motionValue < 0) {//禁止左滑
                            return true;
                        }
                        if (!canRightScroll && motionValue > 0) {//禁止右滑
                            return true;
                        }
                        beforeX = event.getX();//手指移动时，再把当前的坐标作为下一次的‘上次坐标’，解决上述问题
                        break;
                    default:
                        break;
                }
            }
            return super.dispatchTouchEvent(event);
        } else {
            return false;
        }
    }
}

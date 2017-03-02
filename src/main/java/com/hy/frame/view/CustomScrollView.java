package com.hy.frame.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * author HeYan
 * time 2016/8/11 10:08
 */
public class CustomScrollView extends ScrollView {

    private ScrollListener scrollListener;
    private TouchListener touchListener;

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    public void setTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldxX, int oldY) {
        super.onScrollChanged(x, y, oldxX, oldY);
        if (scrollListener != null) {
            scrollListener.onScrollChanged(x, y, oldxX, oldY);
        }
//        if (v != null) {
//            if (y >= 0 && y <= 255) {
//                v.setBackgroundColor(Color.argb(y, 255, 180, 0));
//            }
//        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (touchListener != null)
            touchListener.onTouch(ev);
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
        return super.onTouchEvent(ev);
    }

    public interface ScrollListener {
        void onScrollChanged(int x, int y, int oldxX, int oldY);
    }

    public interface TouchListener {
        void onTouch(MotionEvent event);
    }
}

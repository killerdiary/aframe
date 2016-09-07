package com.hy.frame.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.hy.frame.R;
import com.hy.frame.util.MyLog;

/**
 * @author HeYan
 * @title
 * @time 2015/11/10 17:20
 */
public class SwipeView extends FrameLayout implements View.OnClickListener {
    private NewMyListView.OnMlvSwipeListener listener;
    private View contentView;
    private View menuView;
    private int baseX;
    private int position;
    private int state;
    private static final int FLAG_CLOSE = 0;
    private static final int FLAG_OPEN = 1;
    private ScrollerCompat openScroller;
    private ScrollerCompat closeScroller;

    public SwipeView(Context context) {
        super(context);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SwipeView(View contentView, View menuView, NewMyListView.OnMlvSwipeListener listener) {
        super(contentView.getContext());
        this.contentView = contentView;
        this.menuView = menuView;
        this.listener = listener;
        init();
    }

    private void init() {
        closeScroller = ScrollerCompat.create(getContext());
        openScroller = ScrollerCompat.create(getContext());
        setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        LayoutParams contentFlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        contentView.setLayoutParams(contentFlp);
        contentView.setId(R.id.swipe_vContent);
        addView(contentView);
        LayoutParams menuFlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        menuView.setLayoutParams(menuFlp);
        menuView.setId(R.id.swipe_vMenu);
        if (menuView instanceof ViewGroup) {
            MyLog.e("ViewGroup");
        } else {
            menuView.setOnClickListener(this);
        }
        addView(menuView);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void closeMenu() {
        if (state == FLAG_OPEN) {
            state = FLAG_CLOSE;
            swipe(0);
        }
    }

    public void openMenu() {
        openMenu(menuView.getWidth());
    }

    public void openMenu(int distance) {
        if (state == FLAG_CLOSE) state = FLAG_OPEN;
        swipe(menuView.getWidth());
    }

    public void smoothCloseMenu() {
        state = FLAG_CLOSE;
        baseX = -contentView.getLeft();
        closeScroller.startScroll(0, 0, baseX, 0, 350);
        postInvalidate();
    }

    public void smoothOpenMenu() {
        state = FLAG_OPEN;
        openScroller.startScroll(-contentView.getLeft(), 0, menuView.getWidth(), 0, 350);
        postInvalidate();
    }

    public void swipe(int distance) {
        if (distance > menuView.getWidth()) distance = menuView.getWidth();
        if (distance < 0) distance = 0;
        contentView.layout(-distance, contentView.getTop(), contentView.getWidth() - distance, getMeasuredHeight());
        menuView.layout(contentView.getWidth() - distance, menuView.getTop(), contentView.getWidth() + menuView.getWidth() - distance, menuView.getBottom());
    }

    public boolean isOpen() {
        return state == FLAG_OPEN;
    }

    public boolean isCanOpen() {
        if (contentView.getWidth() - menuView.getLeft() > menuView.getWidth() / 2)
            return true;
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        if (state == FLAG_OPEN) {
            if (openScroller.computeScrollOffset()) {
                swipe(openScroller.getCurrX());
                postInvalidate();
            }
        } else {
            if (closeScroller.computeScrollOffset()) {
                swipe(baseX - closeScroller.getCurrX());
                postInvalidate();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        menuView.measure(MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        contentView.layout(0, 0, getMeasuredWidth(),
                contentView.getMeasuredHeight());
        menuView.layout(getMeasuredWidth(), 0,
                getMeasuredWidth() + menuView.getMeasuredWidth(),
                contentView.getMeasuredHeight());
        // setMenuHeight(mContentView.getMeasuredHeight());
        // bringChildToFront(mContentView);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {

        }
    }
}

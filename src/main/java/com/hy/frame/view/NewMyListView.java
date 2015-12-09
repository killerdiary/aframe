package com.hy.frame.view;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.adapter.SwipeAdapter;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;
import com.hy.frame.view.swipe.SwipeMenu;
import com.hy.frame.view.swipe.SwipeMenuLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author heyan
 * @title 自定义ListView(下拉刷新，点击查看更多)
 * @time 2013-6-27 下午2:59:52
 */
public class NewMyListView extends ListView {

    private final static int FLAG_DONE = 0;// 完成
    private final static int FLAG_PULLING = 1;// 拉...
    private final static int FLAG_RELEASE = 2;// 请释放
    private final static int FLAG_REFRESHING = 3;// 正在刷新
    private final static int RATIO = 3;// 移动的比例
    private LayoutInflater inflater;

    private LinearLayout headView;
    private ImageView imgHeadArrow;
    private ProgressBar proHead;
    private TextView txtHeadHint, txtHeadUpdateTime;
    private int headerHeight;

    private LinearLayout footView;
    private ImageView imgFootArrow;
    private ProgressBar proFoot;
    private TextView txtFootHint, txtFootUpdateTime;
    private int footerHeight;
    private int swipeMenuLayouId;
    private OnMlvListener listener;
    private OnMlvSwipeListener swipeListener;
    /**
     * 是否开启下拉刷新
     */
    private boolean refresh;
    /**
     * 是否开启上拉加载更多
     */
    private boolean loadMore;
    /**
     * 是否开启侧滑
     */
    private boolean slipToLeft;
    /**
     * 滚动的方向
     */
    private int direction;
    /**
     * 向上
     */
    private final static int TO_UP = 1;
    /**
     * 向下
     */
    private final static int TO_DOWN = 2;
    /**
     * 向左
     */
    private final static int TO_LEFT = 3;
    /**
     * 向右
     */
    private final static int TO_RIGHT = 4;
    /**
     * 最小距离
     */
    private static final int MIN_DISTANCE = 10;

    public NewMyListView(Context context) {
        super(context);
        init(context);
    }

    public NewMyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化头部和底部
     *
     * @param context Context
     */
    private void init(Context context) {
        // setCacheColorHint(android.R.color.transparent);
        inflater = LayoutInflater.from(context);
        initHeader();
        initFooter();
        initAnim();
    }

    private void initHeader() {
        headView = (LinearLayout) inflater.inflate(R.layout.lv_header, null);
        imgHeadArrow = HyUtil.getView(headView, R.id.lv_imgHeadArrow);
        proHead = HyUtil.getView(headView, R.id.lv_proHead);
        txtHeadHint = HyUtil.getView(headView, R.id.lv_txtHeadHint);
        txtHeadUpdateTime = HyUtil.getView(headView, R.id.lv_txtHeadUpdateTime);
        // measureView(headView);
        // headerHeight = headView.getMeasuredHeight();
        headerHeight = getResources().getDimensionPixelSize(R.dimen.lv_heigth);
        headView.setPadding(0, -headerHeight, 0, 0);
        headView.invalidate();
        addHeaderView(headView, null, false);
    }

    private void initFooter() {
        footView = (LinearLayout) inflater.inflate(R.layout.lv_footer, null);
        imgFootArrow = HyUtil.getView(footView, R.id.lv_imgFootArrow);
        proFoot = HyUtil.getView(footView, R.id.lv_proFoot);
        txtFootHint = HyUtil.getView(footView, R.id.lv_txtFootHint);
        txtFootUpdateTime = HyUtil.getView(footView, R.id.lv_txtFootUpdateTime);
        // measureView(footView);
        // footerHeight = footView.getMeasuredHeight();
        footerHeight = getResources().getDimensionPixelSize(R.dimen.lv_heigth);
        footView.setPadding(0, 0, 0, -headerHeight);
        footView.invalidate();
        addFooterView(footView, null, false);
    }

    private static final int ERROR_RANGE = 10;

    /**
     * 是否在顶部
     */
    private boolean isTop() {
        if (getAdapter() != null && getAdapter().getCount() > 0) {
            if (getFirstVisiblePosition() > 0)
                return false;
            View child = getChildAt(getHeaderViewsCount());
            if (child != null && child.getTop() < ERROR_RANGE)
                return true;
            return false;
        }
        return true;
    }

    /**
     * 是否显示了最后一项
     */
    private boolean isBottom() {
        if (getAdapter() != null && getAdapter().getCount() > 0) {
            //View child = getChildAt(i);
            MyLog.e("getCount():" + getCount());
            MyLog.e("getAdapter().getCount():" + getAdapter().getCount());
            MyLog.e("getChildCount():" + getChildCount());
            MyLog.e("getLastVisiblePosition:" + getLastVisiblePosition());
            if (getLastVisiblePosition() == getAdapter().getCount() - getFooterViewsCount()) {
                View child = getChildAt(getChildCount() - getFooterViewsCount() - 1);
                if (child != null) {
                    int differ = getHeight() - (child.getTop() + child.getHeight());
                    if (differ > -ERROR_RANGE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isRecored;
    //private int startY;
    private int state;
    private RectF startRect;
    private RectF lastRect;
    /**
     * 触摸的Position
     */
    private int touchPosition;
    /**
     * 触摸View
     */
    private SwipeView touchView;

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (direction != FLAG_DONE && state != FLAG_DONE)
//            return true;
//        return super.onInterceptTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onInterceptHoverEvent(MotionEvent event) {
//        if (direction != FLAG_DONE && state != FLAG_DONE)
//            return true;
//        return super.onInterceptHoverEvent(event);
//    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isRefresh() && !isLoadMore() && !isSlipToLeft())
            return super.onTouchEvent(event);
        if (direction != FLAG_DONE && state == FLAG_REFRESHING) {
            switch (direction) {
                case TO_DOWN:
                case TO_UP:
                    return true;
                case TO_LEFT:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        direction = FLAG_DONE;
                        state = FLAG_DONE;
                        int oldPos = touchPosition;
                        touchPosition = pointToPosition((int) event.getX(), (int) event.getY());
                        if (touchPosition == oldPos && touchView != null && touchView.isOpen()) {
                            touchView.closeMenu();
                            return true;
                        }
                        if (touchView != null && touchView.isOpen()) {
                            touchView.smoothCloseMenu();
                            touchView = null;
                            return true;
                        }
                        View v = getChildAt(touchPosition - getFirstVisiblePosition());
                        if (v instanceof SwipeView) touchView = (SwipeView) v;
                        if (touchView != null && touchView.isOpen()) {
                            touchView.smoothCloseMenu();
                            touchView = null;
                            return true;
                        }
                    }
                    break;
            }
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                MyLog.e("onDown | direction=" + direction + " | state=" + state);
                if (direction == FLAG_DONE && state == FLAG_DONE) {
                    // 开始检测
                    isRecored = true;
                    startRect = null;
                    startRect = new RectF();
                    startRect.left = event.getX();
                    startRect.top = event.getY();
                    MyLog.e("onDown 记录当前位置:" + event.getX() + "x" + event.getY());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isRecored || null == startRect)
                    return super.onTouchEvent(event);
                float distanceX = event.getX() - startRect.left;
                float distanceY = event.getY() - startRect.top;
                MyLog.e("distance :" + distanceX + " x " + distanceY);
                //if (true)
                //   return super.onTouchEvent(event);
                if (direction == FLAG_DONE) {
                    if (Math.abs(distanceX) > MIN_DISTANCE || Math.abs(distanceY) > MIN_DISTANCE) {
                        if (Math.abs(distanceX) > Math.abs(distanceY)) {
                            //水平移动
                            if (distanceX < 0) {
                                if (isSlipToLeft()) {
                                    direction = TO_LEFT;
                                    //向左
                                    MyLog.e("onScroll: turn left 向左");
                                }
                            } else {
                                //direction = TO_RIGHT;
                                //向右
                                MyLog.e("onScroll: turn right 向右");
                            }
                        } else if (Math.abs(distanceX) < Math.abs(distanceY)) {
                            //上下移动
                            if (distanceY > 0) {
                                //下拉
                                if (isRefresh() && isTop()) {
                                    direction = TO_DOWN;
                                    MyLog.e("onScroll: turn down 下拉");
                                }
                            } else {
                                if (isLoadMore() && isBottom()) {
                                    direction = TO_UP;
                                    //上拉
                                    MyLog.e("onScroll: turn up 上拉");
                                }
                            }
                        } else {
                            MyLog.e("onScroll 万中无一");
                        }
                    }
                    if (direction > FLAG_DONE) {
                        startRect.left = event.getX();
                        startRect.top = event.getY();
                        return true;
                    }
                    return super.onTouchEvent(event);
                }
                //int abs = 0;
                int mX = (int) distanceX;
                int mY = (int) distanceY;
                if (direction > FLAG_DONE) {
                    switch (direction) {
                        case TO_UP:
                            if (state == FLAG_DONE) {
                                state = FLAG_PULLING;
                                updateFooterUI();
                            } else if (state == FLAG_PULLING || state == FLAG_RELEASE) {
                                footView.setPadding(0, 0, 0, Math.abs(mY / RATIO) - footerHeight);
                                //footView.setPadding(0, 0, 0, Math.abs(mY) - footerHeight);
                                if (lastRect != null && event.getY() - lastRect.top > 0)
                                    smoothScrollToPosition(getCount() - 1);
                                if (state == FLAG_PULLING && footView.getPaddingBottom() > footerHeight / 2) {
                                    state = FLAG_RELEASE;
                                    updateFooterUI();
                                } else if (state == FLAG_RELEASE && footView.getPaddingBottom() < 0) {
                                    state = FLAG_PULLING;
                                    updateFooterUI();
                                }
                            }
                            break;
                        case TO_DOWN:
                            if (state == FLAG_DONE) {
                                state = FLAG_PULLING;
                                updateHeaderUI();
                            } else if (state == FLAG_PULLING || state == FLAG_RELEASE) {
                                headView.setPadding(0, Math.abs(mY / RATIO) - headerHeight, 0, 0);
                                setSelection(0);
                                if (state == FLAG_PULLING && headView.getPaddingTop() > headerHeight / 2) {
                                    state = FLAG_RELEASE;
                                    updateHeaderUI();
                                } else if (state == FLAG_RELEASE && headView.getPaddingTop() < 0) {
                                    state = FLAG_PULLING;
                                    updateHeaderUI();
                                }
                            }
                            break;
                        case TO_LEFT:
                            if (state == FLAG_DONE) {
                                state = FLAG_PULLING;
                                touchPosition = pointToPosition((int) event.getX(), (int) event.getY());
                                View v = getChildAt(touchPosition - getFirstVisiblePosition());
                                if (v instanceof SwipeView) touchView = (SwipeView) v;
                                if (touchView != null) {
                                    touchView.swipe(Math.abs(mX));
                                    //touchView.smoothOpenMenu();
                                    MyLog.e("touchView.openMenu");
                                    //touchView.openMenu();
                                }
                                return true;
                            } else if (state == FLAG_PULLING || state == FLAG_RELEASE) {
                                if (touchView != null && mX <= 0) {
                                    touchView.swipe(Math.abs(mX));
                                    if (state == FLAG_PULLING && touchView.isCanOpen()) {
                                        state = FLAG_RELEASE;
                                    } else if (state == FLAG_RELEASE && !touchView.isCanOpen()) {
                                        state = FLAG_PULLING;
                                    }
                                }
                                return true;
                            }
                            break;
                        case TO_RIGHT:
                            break;
                    }
                    if (lastRect == null)
                        lastRect = new RectF(event.getX(), event.getY(), 0, 0);
                    else {
                        lastRect.left = event.getX();
                        lastRect.top = event.getY();
                    }
                    if (state == FLAG_PULLING) {
                        super.onTouchEvent(event);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                RectF endRect = new RectF();
                endRect.left = event.getX();
                endRect.top = event.getY();
                pullComplete(startRect, endRect);
                break;
        }
        return super.onTouchEvent(event);
    }

    private void pullComplete(RectF startRect, RectF endRect) {
        if (startRect == null || endRect == null)
            return;
        int mX = (int) (endRect.left - startRect.left);
        int mY = (int) (endRect.top - startRect.top);
        MyLog.e("pullComplete:e1 " + startRect.left + " x " + startRect.top);
        MyLog.e("pullComplete:e2 " + endRect.left + " x " + endRect.top);
        MyLog.e("pullComplete:" + mX + " x " + mY);
        if (direction > FLAG_DONE) {
            if (state == FLAG_PULLING) {
                state = FLAG_DONE;
                switch (direction) {
                    case TO_DOWN:
                        updateHeaderUI();
                        break;
                    case TO_UP:
                        updateFooterUI();
                        break;
                    case TO_LEFT:
                        if (touchView != null)
                            touchView.smoothCloseMenu();
                        break;
                    case TO_RIGHT:
                        break;
                }
                direction = FLAG_DONE;
            } else if (state == FLAG_RELEASE) {
                //state = FLAG_REFRESHING;
                switch (direction) {
                    case TO_DOWN:
                        headView.setPadding(0, 0, 0, Math.abs(mY / RATIO) - footerHeight);
                        if (headView.getPaddingBottom() > footerHeight / 2) {
                            smoothScrollToPosition(0);
                            state = FLAG_REFRESHING;
                            if (listener != null)
                                listener.onMlvRefresh(NewMyListView.this, false);
                        } else {
                            state = FLAG_DONE;
                        }
                        updateHeaderUI();
                        break;
                    case TO_UP:
                        footView.setPadding(0, 0, 0, Math.abs(mY / RATIO) - footerHeight);
                        if (footView.getPaddingBottom() > footerHeight / 2) {
                            smoothScrollToPosition(getCount() - 1);
                            state = FLAG_REFRESHING;
                            if (listener != null)
                                listener.onMlvLoadMore(NewMyListView.this);
                        } else {
                            state = FLAG_DONE;
                        }
                        updateFooterUI();
                        break;
                    case TO_LEFT:
                        if (touchView != null) {
                            if (touchView.isCanOpen()) {
                                touchView.smoothOpenMenu();
                                state = FLAG_REFRESHING;
                            } else {
                                state = FLAG_DONE;
                                touchView.smoothCloseMenu();
                            }
                        }
                        break;
                    case TO_RIGHT:
                        break;
                }
            }
        }
    }

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private void initAnim() {
        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);
        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);
    }

    /**
     * 改变header状态
     */
    private void updateHeaderUI() {
        boolean isBack = false;
        switch (state) {
            case FLAG_PULLING:
                proHead.setVisibility(View.GONE);
                txtHeadHint.setVisibility(View.VISIBLE);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                imgHeadArrow.setVisibility(View.VISIBLE);
                if (null != imgHeadArrow.getTag())
                    isBack = (Boolean) imgHeadArrow.getTag();
                if (isBack) {
                    MyLog.e("isBack: " + isBack);
                    imgHeadArrow.setTag(false);
                    imgHeadArrow.clearAnimation();
                    imgHeadArrow.startAnimation(reverseAnimation);
                } else {

                }
                txtHeadHint.setText(R.string.refresh_down_text);
                break;
            case FLAG_RELEASE:
                imgHeadArrow.setVisibility(View.VISIBLE);
                proHead.setVisibility(View.GONE);
                txtHeadHint.setVisibility(View.VISIBLE);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                if (null != imgHeadArrow.getTag())
                    isBack = (Boolean) imgHeadArrow.getTag();
                if (!isBack) {
                    MyLog.e("isBack: " + isBack);
                    imgHeadArrow.setTag(true);
                    imgHeadArrow.clearAnimation();
                    imgHeadArrow.startAnimation(animation);
                }
                txtHeadHint.setText(R.string.refresh_release_text);
                // Log.i("HyLog", "RELEASE_To_REFRESH 这是第  " + i++ + "步" + 12 +
                // "请释放 刷新");
                break;
            case FLAG_REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                proHead.setVisibility(View.VISIBLE);
                imgHeadArrow.clearAnimation();
                imgHeadArrow.setVisibility(View.GONE);
                txtHeadHint.setText(R.string.refresh_load_ing);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                break;
            case FLAG_DONE:
                headView.setPadding(0, -headerHeight, 0, 0);
                proHead.setVisibility(View.GONE);
                imgHeadArrow.clearAnimation();
                txtHeadHint.setText(R.string.refresh_load_complete);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 改变footer状态
     */
    private void updateFooterUI() {
        boolean isBack = false;
        switch (state) {
            case FLAG_PULLING:
                proFoot.setVisibility(View.GONE);
                txtFootHint.setVisibility(View.VISIBLE);
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                imgFootArrow.setVisibility(View.VISIBLE);
                if (null != imgFootArrow.getTag())
                    isBack = (Boolean) imgFootArrow.getTag();
                if (isBack) {
                    imgFootArrow.setTag(false);
                    imgFootArrow.clearAnimation();
                    imgFootArrow.startAnimation(reverseAnimation);
                }
                txtFootHint.setText(R.string.refresh_load_more);
                break;
            case FLAG_RELEASE:
                imgFootArrow.setVisibility(View.VISIBLE);
                proFoot.setVisibility(View.GONE);
                txtFootHint.setVisibility(View.VISIBLE);
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                if (null != imgFootArrow.getTag())
                    isBack = (Boolean) imgFootArrow.getTag();
                if (!isBack) {
                    MyLog.e("isBack: " + isBack);
                    imgFootArrow.setTag(true);
                    imgFootArrow.clearAnimation();
                    imgFootArrow.startAnimation(animation);
                }
                txtFootHint.setText(R.string.refresh_load_more_release);
                break;
            case FLAG_REFRESHING:
                footView.setPadding(0, 0, 0, 0);
                proFoot.setVisibility(View.VISIBLE);
                imgFootArrow.clearAnimation();
                imgFootArrow.setVisibility(View.GONE);
                txtFootHint.setText(R.string.refresh_load_ing);
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                break;
            case FLAG_DONE:
                footView.setPadding(0, -1 * footerHeight, 0, 0);
                proFoot.setVisibility(View.GONE);
                imgFootArrow.clearAnimation();
                txtFootHint.setText(R.string.refresh_load_complete);
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setSwipeMenuLayouId(int swipeMenuLayouId) {
        this.swipeMenuLayouId = swipeMenuLayouId;
    }

    public void setAdapter(ListAdapter adapter) {
        if (swipeMenuLayouId == 0) {
            MyLog.e("Please Called setSwipeMenuLayouId()");
            super.setAdapter(adapter);
            return;
        }
        txtHeadUpdateTime.setText(getResources().getString(R.string.refresh_last_time, getNowTime()));
        txtFootUpdateTime.setText(getResources().getString(R.string.refresh_last_time, getNowTime()));
        super.setAdapter(new SwipeAdapter(getContext(), adapter, swipeMenuLayouId, new OnMlvSwipeListener() {
            @Override
            public void onMlvSwipeClick(int position, SwipeMenu menu, int index) {

            }

            @Override
            public void onMlvRefresh(NewMyListView lv, boolean first) {

            }

            @Override
            public void onMlvLoadMore(NewMyListView lv) {

            }
        }));
    }

    /**
     * 手动调用下拉刷新
     */
    public void onRefresh() {
        state = FLAG_REFRESHING;
        direction = TO_DOWN;
        updateHeaderUI();
        if (listener != null)
            listener.onMlvRefresh(this, false);
    }

    /**
     * 刷新完成
     */
    public void onRefreshComplete() {
        state = FLAG_DONE;
        switch (direction) {
            case TO_DOWN:
                txtHeadUpdateTime.setText(getResources().getString(R.string.refresh_last_time, getNowTime()));
                updateHeaderUI();
                break;
            case TO_UP:
                txtFootUpdateTime.setText(getResources().getString(R.string.refresh_last_time, getNowTime()));
                updateFooterUI();
                break;
            case TO_LEFT:

                break;
            case TO_RIGHT:
                break;
        }
        direction = FLAG_DONE;
    }

    /**
     * 获取当前时间Date
     *
     * @return 现在时间(Now)
     */
    public String getNowTime() {
        Date d = new Date(System.currentTimeMillis());
        // String type = "yyyy-MM-dd HH:mm:ss";
        String type = "HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(type, Locale.CHINA);
        return formatter.format(d);
    }

    //*********************************************

    public boolean isSlipToLeft() {
        return slipToLeft;
    }

    public void setSlipToLeft(boolean slipToLeft) {
        this.slipToLeft = slipToLeft;
    }

    public boolean isLoadMore() {
        return loadMore;
    }

    public void setLoadMore(boolean loadMore) {
        this.loadMore = loadMore;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    //*******************************************************************
    public interface OnMlvListener {
        /**
         * 下拉刷新
         *
         * @param lv
         * @param first 是否是第一次调用
         */
        void onMlvRefresh(NewMyListView lv, boolean first);

        /**
         * 加载更多
         *
         * @param lv
         */
        void onMlvLoadMore(NewMyListView lv);
    }

    public interface OnMlvSwipeListener extends OnMlvListener {
        /**
         * 滑块点击
         *
         * @param position
         * @param menu
         * @param index
         */
        void onMlvSwipeClick(int position, SwipeMenu menu, int index);
    }

    /**
     * 设置监听事件
     *
     * @param listener OnMlvListener or OnMlvSwipeListener
     */
    public void setListener(OnMlvListener listener) {
        this.listener = listener;
    }


}
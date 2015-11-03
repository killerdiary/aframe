package com.hy.frame.view;

import android.content.Context;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;
import com.hy.frame.util.MyToast;
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

    // private final static int FLAG_LOADING = 4;// 加载中
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

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;


    private OnMlvListener listener;

    /**
     * 是否滚动到顶部
     */
    private boolean scrollTop;
    /**
     * 是否滚动到底部
     */
    private boolean scrollBottom;


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

    private GestureDetectorCompat detector;

    /**
     * 初始化头部和底部
     *
     * @param context
     */
    private void init(Context context) {
        // setCacheColorHint(android.R.color.transparent);
        inflater = LayoutInflater.from(context);
        initHeader();
        initFooter();
        initAnim();
        state = FLAG_DONE;
        pullDownRefresh = false;
        pullUpRefresh = false;
        detector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                MyLog.e("onDown");
                if (!pullDownRefresh && !pullUpRefresh && !sideSlip)
                    return false;
                if (direction == FLAG_DONE && state == FLAG_DONE) {
                    // 开始检测
                    isRecored = true;
                    startEvent = e;
                    MyLog.e("onDown 记录当前位置:" + e.getX() + "x" + e.getY());
                }
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //MyLog.e("onScroll: distanceX:" + distanceX + "|distanceY:" + distanceY + "|e2:" + e2.getAction());
                //MyLog.e("onScroll: e1:" + e1.getX() + "x" + e1.getY() + "|e2:" + e2.getX() + "x" + e2.getY());
                if (!isRecored) {
                    MyLog.e("onScroll: isRecored:" + isRecored);
                    return true;
                }
                if (direction == FLAG_DONE) {
                    if (Math.abs(distanceX) > MIN_DISTANCE || Math.abs(distanceY) > MIN_DISTANCE) {
                        if (Math.abs(distanceX) > Math.abs(distanceY)) {
                            //水平移动
                            if (distanceX > 0) {
                                if (sideSlip)
                                    direction = TO_LEFT;
                                //向左
                                MyLog.e("onScroll: turn left 向左");
                            } else {
                                //direction = TO_RIGHT;
                                //向右
                                MyLog.e("onScroll: turn right 向右");
                            }
                        } else if (Math.abs(distanceX) < Math.abs(distanceY)) {
                            //上下移动
                            if (distanceY > 0) {
                                if (pullUpRefresh && isBottom())
                                    direction = TO_UP;
                                //上拉
                                MyLog.e("onScroll: turn up 上拉");
                            } else {
                                if (pullDownRefresh && isTop())
                                    direction = TO_DOWN;
                                //下拉
                                MyLog.e("onScroll: turn down 下拉");
                            }
                        } else {
                            MyLog.e("onScroll 万中无一");
                        }
                    }
                } else {
                    //int abs = 0;
                    int mX = (int) (e2.getX() - e1.getX());
                    int mY = (int) (e2.getY() - e1.getY());
                    switch (direction) {
                        case TO_UP:
                            //abs = (int) Math.abs(distanceX);
                            switch (state) {
                                case FLAG_DONE:
                                    //判断是否可进入上拉
                                    if (isBottom()) {
                                        MyLog.e("onScroll isBottom");
                                        state = FLAG_PULLING;
                                        updateFooterUI();
                                    }
                                    break;
                                case FLAG_PULLING:
                                    footView.setPadding(0, 0, 0, Math.abs(mY / RATIO) - footerHeight);
                                    if (footView.getPaddingBottom() > footerHeight / 2) {
                                        state = FLAG_RELEASE;
                                        isBack = true;
                                        updateFooterUI();
                                    }
                                    break;
                                case FLAG_RELEASE:
                                    footView.setPadding(0, 0, 0, Math.abs(mY / RATIO) - footerHeight);
                                    if (footView.getPaddingBottom() < footerHeight / 2) {
                                        state = FLAG_PULLING;
                                        isBack = false;
                                        updateFooterUI();
                                    }
                                    break;
                                case FLAG_REFRESHING:
                                    break;

                            }
                            break;
                        case TO_DOWN:
                            switch (state) {
                                case FLAG_DONE:
                                    //判断是否可进入下拉
                                    if (isTop()) {
                                        MyLog.e("onScroll isTop");
                                        state = FLAG_PULLING;
                                        updateHeaderUI();
                                    }
                                    break;
                                case FLAG_PULLING:
                                    headView.setPadding(0, Math.abs(mY / RATIO) - headerHeight, 0, 0);
                                    //MyLog.e("headView.getPaddingTop():" + headView.getPaddingTop());
                                    if (headView.getPaddingTop() > headerHeight / 2) {
                                        state = FLAG_RELEASE;
                                        isBack = true;
                                        updateHeaderUI();
                                    }
                                case FLAG_RELEASE:

                                    break;
                                case FLAG_REFRESHING:
                                    break;

                            }
                            break;
                        case TO_LEFT:
                            if (state == 0) {
                                //判断是否可进入左滑
                                state = FLAG_PULLING;
                            }
                            break;
                        case TO_RIGHT:
                            break;
                    }
                }
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                MyLog.e("onFling:" + velocityX + "x" + velocityY);
                pullComplete(e1, e2);
                return true;
            }
        }

        );
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
     * 是否在顶部
     */
    private boolean isTop() {
        if (getAdapter() != null && getAdapter().getCount() > 0) {
            if (getFirstVisiblePosition() > 0)
                return false;
        }
        return true;
    }

    private static final int ERROR_RANGE = 10;

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
    private boolean isBack;
    private MotionEvent startEvent;

    private void pullComplete(MotionEvent e1, MotionEvent e2) {
        int mX = (int) (e2.getX() - e1.getX());
        int mY = (int) (e2.getY() - e1.getY());
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

                        break;
                    case TO_RIGHT:
                        break;
                }
                direction = FLAG_DONE;
            } else if (state == FLAG_RELEASE) {
                //state = FLAG_REFRESHING;
                switch (direction) {
                    case TO_DOWN:
                        updateHeaderUI();
                        if (listener != null)
                            listener.onMlvRefresh(NewMyListView.this, false);
                        break;
                    case TO_UP:
                        footView.setPadding(0, 0, 0, Math.abs(mY / RATIO) - footerHeight);
                        if (footView.getPaddingBottom() > footerHeight / 2) {
                            state = FLAG_REFRESHING;
                            isBack = false;
                            if (listener != null)
                                listener.onMlvLoadMore(NewMyListView.this);
                        } else {
                            state = FLAG_DONE;
                            isBack = false;
                        }
                        updateFooterUI();
                        break;
                    case TO_LEFT:

                        break;
                    case TO_RIGHT:
                        break;
                }
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (detector != null)
            detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pullComplete(startEvent, event);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 改变header状态
     */
    private void updateHeaderUI() {
        switch (state) {
            case FLAG_PULLING:
                proHead.setVisibility(View.GONE);
                txtHeadHint.setVisibility(View.VISIBLE);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                imgHeadArrow.clearAnimation();
                imgHeadArrow.setVisibility(View.VISIBLE);
                if (isBack) {
                    isBack = false;
                    imgHeadArrow.clearAnimation();
                    imgHeadArrow.startAnimation(reverseAnimation);
                }
                txtHeadHint.setText(R.string.refresh_down_text);
                break;
            case FLAG_RELEASE:
                imgHeadArrow.setVisibility(View.VISIBLE);
                proHead.setVisibility(View.GONE);
                txtHeadHint.setVisibility(View.VISIBLE);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                imgHeadArrow.clearAnimation();
                imgHeadArrow.setImageResource(R.drawable.refresh_arrow_top);
                imgHeadArrow.startAnimation(animation);
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
                imgHeadArrow.setImageResource(R.drawable.refresh_arrow_top);
                txtHeadHint.setText(R.string.refresh_load_complete);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 改变footer状态
     */
    private void updateFooterUI() {
        switch (state) {
            case FLAG_PULLING:
                proFoot.setVisibility(View.GONE);
                txtFootHint.setVisibility(View.VISIBLE);
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                imgFootArrow.clearAnimation();
                imgFootArrow.setVisibility(View.VISIBLE);
                if (isBack) {
                    isBack = false;
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
                imgFootArrow.clearAnimation();
                //imgFootArrow.setImageResource(R.drawable.refresh_arrow_get);
                imgFootArrow.startAnimation(animation);
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
                imgFootArrow.setImageResource(R.drawable.refresh_arrow_top);
                txtFootHint.setText(R.string.refresh_load_complete);
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        txtHeadUpdateTime.setText(getResources().getString(R.string.refresh_last_time, getNowTime()));
        txtFootUpdateTime.setText(getResources().getString(R.string.refresh_last_time, getNowTime()));
        super.setAdapter(adapter);
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
    /**
     * 触摸的Position
     */
    private int mTouchPosition;
    /**
     * 触摸View
     */
    private SwipeMenuLayout mTouchView;

    /**
     * 打开侧滑
     *
     * @param position
     */
    public void smoothOpenMenu(int position) {
        if (isSideSlip() && position >= getFirstVisiblePosition() && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof SwipeMenuLayout) {
                mTouchPosition = position;
                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                }
                mTouchView = (SwipeMenuLayout) view;
                mTouchView.smoothOpenMenu();
            }
        }
    }

    private OnSwipeListener mOnSwipeListener;

    /**
     * 监听侧滑开始结束
     *
     * @param onSwipeListener
     */
    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;
    }

    public interface OnSwipeListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }

    //*******************************************************************
    /**
     * 是否开启下拉刷新
     */
    private boolean pullDownRefresh;
    /**
     * 是否开启上拉加载更多
     */
    private boolean pullUpRefresh;
    /**
     * 是否开启侧滑
     */
    private boolean sideSlip;

    public boolean isPullDownRefresh() {
        return pullDownRefresh;
    }

    public void setPullDownRefresh(boolean pullDownRefresh) {
        this.pullDownRefresh = pullDownRefresh;
    }

    public boolean isPullUpRefresh() {
        return pullUpRefresh;
    }

    public void setPullUpRefresh(boolean pullUpRefresh) {
        this.pullUpRefresh = pullUpRefresh;
    }

    public boolean isSideSlip() {
        return sideSlip;
    }

    public void setSideSlip(boolean sideSlip) {
        this.sideSlip = sideSlip;
    }

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
package com.hy.frame.view;

import android.content.Context;
import android.graphics.Rect;
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
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.hy.frame.R;
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
public class NewMyListView extends ListView implements OnScrollListener {

    private final static int FLAG_DONE = 0;// 完成
    private final static int FLAG_PULLING = 1;// 拉...
    private final static int FLAG_REFRESHING = 2;// 正在刷新
    private final static int FLAG_RELEASE = 3;// 请释放
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
        setOnScrollListener(this);
        state = FLAG_DONE;
        pullDownRefresh = false;
        pullUpRefresh = false;
        detector = new GestureDetectorCompat(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                if (!pullDownRefresh && !pullUpRefresh && !sideSlip)
                    return false;
                start.left = 0;
                start.top = 0;
                direction = 0;
                //未正常结束
                if (isRecored) {
                    isRecored = false;
                    MyLog.e("onDown 未正常结束:");
                    return false;
                } else {
                    // 开始检测
                    isRecored = true;
                    start.left = e.getX();
                    start.top = e.getY();
                    MyLog.e("onDown 记录当前位置:" + start.left + "x" + start.top);
                }
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                MyLog.e("onShowPress");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                MyLog.e("onSingleTapUp");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //MyLog.e("onScroll: distanceX:" + distanceX + "|distanceY:" + distanceY);
                if (!isRecored)
                    return true;
                if (direction == 0) {
                    if (Math.abs(distanceX) > MIN_DISTANCE || Math.abs(distanceY) > MIN_DISTANCE) {
                        if (Math.abs(distanceX) > Math.abs(distanceY)) {
                            //水平移动
                            if (distanceX > 0) {
                                if (sideSlip)
                                    direction = TO_LEFT;
                                //向左
                                MyLog.e("onFling: turn left 向左");
                            } else {
                                //direction = TO_RIGHT;
                                //向右
                                MyLog.e("onFling: turn right 向右");
                            }
                        } else if (Math.abs(distanceX) < Math.abs(distanceY)) {
                            //上下移动
                            if (distanceY > 0) {
                                if (pullUpRefresh && isBottom())
                                    direction = TO_UP;
                                //上拉
                                MyLog.e("onFling: turn up 上拉");
                            } else {
                                if (pullDownRefresh && isTop())
                                    direction = TO_DOWN;
                                //下拉
                                MyLog.e("onFling: turn down 下拉");
                            }
                        } else {
                            MyLog.e("onScroll 万中无一");
                        }
                    }
                } else {
                    int abs = 0;
                    switch (direction) {
                        case TO_UP:
                            abs = (int) Math.abs(distanceX);
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
                                    pppppp++;
                                    footView.setPadding(0, 0, 0,pppppp);
                                    break;
                                case FLAG_REFRESHING:
                                    break;
                                case FLAG_RELEASE:
                                    break;
                            }
                            break;
                        case TO_DOWN:
                            if (state == 0) {
                                //判断是否可进入下拉
                                if (isTop()) {
                                    state = FLAG_PULLING;

                                }
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
            public void onLongPress(MotionEvent e) {
                MyLog.e("onLongPress");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                MyLog.e("onFling: velocityX:" + velocityX + "|velocityY:" + velocityY);
//                int verticalMinistance = 5;
//                int minVelocity = 5;
//                if (e1.getX() - e2.getX() > verticalMinistance &&
//                        Math.abs(velocityX) > minVelocity) {
//                    MyLog.e("onFling: turn left");
//                } else if (e2.getX() - e1.getX() > verticalMinistance &&
//                        Math.abs(velocityX) > minVelocity) {
//                    MyLog.e("onFling: turn right");
//                } else if (e1.getY() - e2.getY() > 20 && Math.abs(velocityY) >
//                        10) {
//                    MyLog.e("onFling: turn up");
//                } else if (e2.getY() - e1.getY() > 20 && Math.abs(velocityY) >
//                        10) {
//                    MyLog.e("onFling: turn down");
//                }
                return false;
            }
        }

        );
    }
private int pppppp;
    private void initHeader() {
        headView = (LinearLayout) inflater.inflate(R.layout.lv_header, null);
        imgHeadArrow = HyUtil.getView(headView, R.id.lv_imgHeadArrow);
        proHead = HyUtil.getView(headView, R.id.lv_proHead);
        txtHeadHint = HyUtil.getView(headView, R.id.lv_txtHeadHint);
        txtHeadUpdateTime = HyUtil.getView(headView, R.id.lv_txtHeadUpdateTime);
        // measureView(headView);
        // headerHeight = headView.getMeasuredHeight();
        headerHeight = getResources().getDimensionPixelSize(R.dimen.lv_heigth);
        headView.setPadding(0, -1 * headerHeight, 0, 0);
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
        footView.setPadding(0, 0, 0, -1 * headerHeight);
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

    public void onScroll(AbsListView arg0, int firstVisiableItem,
                         int visibleItemCount, int totalItemCount) {
        if (state == FLAG_DONE) {
            if (firstVisiableItem == 0) {
                scrollTop = true;
            } else {
                scrollTop = false;
            }
            // MyLog.d("最后位置: " + getLastVisiblePosition() + " 总：" + totalItemCount);
            if (getLastVisiblePosition() == totalItemCount - 1) {
                scrollBottom = true;
            } else {
                scrollBottom = false;
            }
        }
    }

    public void onScrollStateChanged(AbsListView arg0, int arg1) {
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

    /**
     * 是否显示了最后一项
     */
    private boolean isBottom() {
        if (getAdapter() != null && getAdapter().getCount() > 0) {
            //final View child = getChildAt(i);
            MyLog.e("getCount():" + getCount());
            MyLog.e("getAdapter().getCount():" + getAdapter().getCount());
            MyLog.e("getChildCount():" + getChildCount());
            MyLog.e("getLastVisiblePosition:" + getLastVisiblePosition());
            if (getLastVisiblePosition() + 1 == getAdapter().getCount()){

                return true;
            }
        }
        return false;
    }

    private boolean isRecored;
    //private int startY;
    private int state;
    private boolean isBack;
    private RectF start = new RectF();

    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if (true)
            return super.onTouchEvent(event);
        //GestureDetector
        if (pullDownRefresh || pullUpRefresh || sideSlip) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    start.left = 0;
                    start.top = 0;
                    direction = 0;
                    //未正常结束
                    if (isRecored) {
                        isRecored = false;
                        MyLog.e("ACTION_DOWN 未正常结束:");
                        return super.onTouchEvent(event);
                    } else {
                        // 开始检测
                        isRecored = true;
                        start.left = event.getX();
                        start.top = event.getY();
                        MyLog.e("ACTION_DOWN 记录当前位置:" + start.left + "x" + start.top);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isRecored) {
                        start.left = 0;
                        start.top = 0;
                        direction = 0;
                        MyLog.e("ACTION_MOVE 未正常开始:");
                        return super.onTouchEvent(event);
                    }
                    int minVelocity = 5;
                    int tempY = (int) event.getY();
                    // 检测开启，没有刷新，没有加载
//                    if (state != FLAG_REFRESHING && isRecored) {
//                        // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
//                        int distance = tempY - startY;
//                        int abs = Math.abs(distance);
//                        if (state == FLAG_DONE) {
//                            if (distance != 0) {
//                                if (distance > 0)
//                                    direction = TO_DOWN;
//                                else
//                                    direction = TO_UP;
//                                state = FLAG_PULLING;
//                                changeViewByState();
//                            }
//                            // MyLog.e("ACTION_MOVE FLAG_DONE" + direction);
//                        }
//                        if (direction == TO_DOWN && !pullDownRefresh) {
//                            state = FLAG_DONE;
//                            break;
//                        }
//                        if (direction == TO_UP && !pullUpRefresh) {
//                            state = FLAG_DONE;
//                            break;
//                        }
//                        if (state == FLAG_PULLING) {
//                            // MyLog.e("ACTION_MOVE FLAG_PULLING " + direction);
//                            if (direction == TO_DOWN) {
//                                if (distance / RATIO >= headerHeight) {
//                                    state = FLAG_RELEASE;
//                                    isBack = true;
//                                    changeViewByState();
//                                } else if (distance <= 0) {
//                                    state = FLAG_DONE;
//                                    changeViewByState();
//                                }
//                                headView.setPadding(0, distance / RATIO
//                                        - headerHeight, 0, 0);
//                            } else if (direction == TO_UP) {
//                                if (abs / RATIO >= footerHeight) {
//                                    state = FLAG_RELEASE;
//                                    isBack = true;
//                                    changeViewByState();
//                                } else if (distance >= 0) {
//                                    state = FLAG_DONE;
//                                    changeViewByState();
//                                }
//                                footView.setPadding(0, 0, 0, abs / RATIO - 1
//                                        * footerHeight);
//                            }
//                        }
//                        // 可以松手去刷新了
//                        if (state == FLAG_RELEASE) {
//                            // MyLog.e("ACTION_MOVE 可以松手去刷新了");
//                            if (direction == TO_DOWN) {
//                                // setSelection(0);
//                                // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
//                                if ((distance / RATIO < headerHeight)
//                                        && distance > 0) {
//                                    state = FLAG_PULLING;
//                                    changeViewByState();
//                                } else if (distance <= 0) {
//                                    state = FLAG_DONE;
//                                    changeViewByState();
//                                }
//                                headView.setPadding(0, distance / RATIO
//                                        - headerHeight, 0, 0);
//                            } else if (direction == TO_UP) {
//                                if ((abs / RATIO < footerHeight) && distance < 0) {
//                                    state = FLAG_PULLING;
//                                    changeViewByState();
//                                } else if (distance >= 0) {
//                                    state = FLAG_DONE;
//                                    changeViewByState();
//                                }
//                                footView.setPadding(0, 0, 0, abs / RATIO - 1
//                                        * footerHeight);
//                            }
//                        }
//                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isRecored) {
                        MyLog.e("ACTION_UP 未正常结束:");
                        return super.onTouchEvent(event);
                    }
                    if (direction > 0) {
                        if (direction == TO_LEFT) {
                            if (mTouchView != null) {
                                mTouchView.onSwipe(event);
                                if (!mTouchView.isOpen()) {
                                    mTouchPosition = -1;
                                    mTouchView = null;
                                }
                            }
                            if (mOnSwipeListener != null) {
                                mOnSwipeListener.onSwipeEnd(mTouchPosition);
                            }
                        } else if (direction == TO_DOWN) {
                            //取消下拉
                            if (state == FLAG_PULLING) {
                                state = FLAG_DONE;
                                updateHeaderUI();
                            }
                            //等待释放
                            else if (state == FLAG_RELEASE) {
                                state = FLAG_REFRESHING;
                                updateHeaderUI();
                                if (listener != null)
                                    listener.onMlvRefresh(this, false);
                            }
                        } else if (direction == TO_UP) {

                        }
                        if (state != FLAG_REFRESHING) {
                            if (state == FLAG_DONE) {

                            }
                            if (state == FLAG_PULLING) {
                                state = FLAG_DONE;
                                changeViewByState();
                            }
                            if (state == FLAG_RELEASE) {
                                state = FLAG_REFRESHING;
                                changeViewByState();
                                if (direction == TO_DOWN) {
                                    if (listener != null) {
                                        listener.onMlvRefresh(this, false);
                                    }
                                } else if (direction == TO_UP) {
                                    if (listener != null) {
                                        listener.onMlvRefresh(this, false);
                                    }
                                }
                            }
                        }
                        isRecored = false;
                        isBack = false;
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        super.onTouchEvent(event);
                        return true;
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void changeViewByState() {

    }

    /**
     * 改变header状态
     */
    private void updateHeaderUI() {
        switch (state) {
            case FLAG_RELEASE:
                imgHeadArrow.setVisibility(View.VISIBLE);
                proHead.setVisibility(View.GONE);
                txtHeadHint.setVisibility(View.VISIBLE);
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                imgHeadArrow.clearAnimation();
                // imgHeadArrow.setImageResource(R.drawable.refresh_arrow_get);
                imgHeadArrow.startAnimation(animation);
                txtHeadHint.setText("请释放 刷新");
                // Log.i("HyLog", "RELEASE_To_REFRESH 这是第  " + i++ + "步" + 12 +
                // "请释放 刷新");
                break;
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
                    txtHeadHint.setText("下拉刷新");
                } else {
                    txtHeadHint.setText("下拉刷新");
                }
                break;
            case FLAG_REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                proHead.setVisibility(View.VISIBLE);
                imgHeadArrow.clearAnimation();
                imgHeadArrow.setVisibility(View.GONE);
                txtHeadHint.setText("正在加载中 ...");
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                break;
            case FLAG_DONE:
                headView.setPadding(0, -1 * headerHeight, 0, 0);
                proHead.setVisibility(View.GONE);
                imgHeadArrow.clearAnimation();
                imgHeadArrow.setImageResource(R.drawable.refresh_arrow_top);
                txtHeadHint.setText("已经加载完毕 ");
                txtHeadUpdateTime.setVisibility(View.VISIBLE);
                break;
        }


    }

    /**
     * 改变footer状态
     */
    private void updateFooterUI() {
        switch (state) {
            case FLAG_RELEASE:
                imgFootArrow.setVisibility(View.VISIBLE);
                proFoot.setVisibility(View.GONE);
                txtFootHint.setVisibility(View.VISIBLE);
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                imgFootArrow.clearAnimation();
                // imgFootArrow.setImageResource(R.drawable.refresh_arrow_get);
                imgFootArrow.startAnimation(animation);
                txtFootHint.setText("请释放 加载更多");
                break;
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
                    txtFootHint.setText("上拉加载更多");
                } else {
                    txtFootHint.setText("上拉加载更多");
                }
                break;
            case FLAG_REFRESHING:
                // footView.setPadding(0, 0, 0, 0);
                proFoot.setVisibility(View.VISIBLE);
                imgFootArrow.clearAnimation();
                imgFootArrow.setVisibility(View.GONE);
                txtFootHint.setText("正在加载中 ...");
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                break;
            case FLAG_DONE:
                footView.setPadding(0, -1 * footerHeight, 0, 0);
                proFoot.setVisibility(View.GONE);
                imgFootArrow.clearAnimation();
                imgFootArrow.setImageResource(R.drawable.refresh_arrow_top);
                txtFootHint.setText("已经加载完毕 ");
                txtFootUpdateTime.setVisibility(View.VISIBLE);
                break;
        }


    }

    public void setAdapter(BaseAdapter adapter) {
        txtHeadUpdateTime.setText("更新时间：" + getNowTime());
        txtFootUpdateTime.setText("更新时间：" + getNowTime());
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
        if (direction == TO_DOWN) {
            txtHeadUpdateTime.setText("上次更新: " + getNowTime());
        } else if (direction == TO_UP) {
            txtFootUpdateTime.setText("上次更新: " + getNowTime());
        }
        updateHeaderUI();
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
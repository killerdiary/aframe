package com.hy.frame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;

/**
 * 带刷新的LinearLayout
 * 
 * @author HeYan
 * @time 2014年12月31日 下午12:29:10
 */
public class RefreshLinearLayout extends LinearLayout {

    private Scroller scroller;

    private View headView;
    private ImageView imgHeadArrow;

    private ProgressBar proHead;
    private TextView txtHeadHint;
    private TextView txtHeadUpdateTime;
    private int headMarginTop;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;

    private RefreshListener listener;

    private Long refreshTime = null;
    private int lastX;
    private int lastY;
    // 拉动标记
    private boolean isDragging = false;
    // 是否可刷新标记
    private boolean isRefreshEnabled = true;
    // 在刷新中标记
    private boolean isRefreshing = false;

    public RefreshLinearLayout(Context context) {
        super(context);

    }

    public RefreshLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAnim();
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        scroller = new Scroller(context);
        headView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.in_lv_header, null);
        imgHeadArrow = HyUtil.getView(headView, R.id.lv_imgHeadArrow);
        proHead = HyUtil.getView(headView, R.id.lv_proHead);
        txtHeadHint = HyUtil.getView(headView, R.id.lv_txtHeadHint);
        txtHeadUpdateTime = HyUtil.getView(headView, R.id.lv_txtHeadUpdateTime);
        // measureView(headView);
        // headerHeight = headView.getMeasuredHeight();
        int headerHeight = getResources().getDimensionPixelSize(R.dimen.lv_heigth);
        headMarginTop = -headerHeight;
        // headView.setPadding(0, -1 * headerHeight, 0, 0);
        headView.invalidate();
        // addHeaderView(headView, null, false);
        LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, -headerHeight);
        lp.topMargin = headMarginTop;
        lp.gravity = Gravity.CENTER;
        addView(headView, lp);
    }

    private void initAnim() {
        animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);
        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) event.getRawY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // 记录下y坐标
            lastY = y;
            break;

        case MotionEvent.ACTION_MOVE:
            MyLog.i("ACTION_MOVE");
            // y移动坐标
            int m = y - lastY;
            if (((m < 6) && (m > -1)) || (!isDragging)) {
                doMovement(m);
            }
            // 记录下此刻y坐标
            this.lastY = y;
            break;

        case MotionEvent.ACTION_UP:
            MyLog.i("ACTION_UP");

            fling();

            break;
        }
        return true;
    }

    /**
     * up事件处理
     */
    private void fling() {
        LinearLayout.LayoutParams lp = (LayoutParams) headView.getLayoutParams();
        MyLog.i("fling()" + lp.topMargin);
        if (lp.topMargin > 0) {// 拉到了触发可刷新事件
            refresh();
        } else {
            returnInitState();
        }
    }

    private void returnInitState() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.headView.getLayoutParams();
        int i = lp.topMargin;
        scroller.startScroll(0, i, 0, headMarginTop);
        invalidate();
    }

    private void refresh() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.headView.getLayoutParams();
        int i = lp.topMargin;
        imgHeadArrow.clearAnimation();
        imgHeadArrow.setVisibility(View.GONE);
        proHead.setVisibility(View.VISIBLE);
        txtHeadHint.setText(R.string.refresh_doing);
        scroller.startScroll(0, i, 0, 0 - i);
        invalidate();
        if (listener != null) {
            listener.onRefresh(this);
            isRefreshing = true;
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int i = this.scroller.getCurrY();
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.headView.getLayoutParams();
            int k = Math.max(i, headMarginTop);
            lp.topMargin = k;
            this.headView.setLayoutParams(lp);
            this.headView.invalidate();
            invalidate();
        }
    }

    /**
     * 下拉move事件处理
     * 
     * @param moveY
     */
    private void doMovement(int moveY) {
        LinearLayout.LayoutParams lp = (LayoutParams) headView.getLayoutParams();
        if (moveY > 0) {
            // 获取view的上边距
            float f1 = lp.topMargin;
            float f2 = moveY * 0.3F;
            int i = (int) (f1 + f2);
            // 修改上边距
            lp.topMargin = i;
            // 修改后刷新
            headView.setLayoutParams(lp);
            headView.invalidate();
            invalidate();
        }
        proHead.setVisibility(View.GONE);
        imgHeadArrow.setVisibility(View.VISIBLE);
        if (lp.topMargin > 0) {
            txtHeadHint.setText(R.string.refresh_release_text);
            imgHeadArrow.setImageResource(R.mipmap.refresh_arrow_top);
        } else {
            txtHeadHint.setText(R.string.refresh_down_text);
            imgHeadArrow.clearAnimation();
            // imgHeadArrow.setImageResource(R.drawable.refresh_arrow_get);
            imgHeadArrow.startAnimation(animation);

        }

    }

    public void setRefreshEnabled(boolean b) {
        this.isRefreshEnabled = b;
    }

    public void setListener(RefreshListener listener) {
        this.listener = listener;
        proHead.setVisibility(View.GONE);
        txtHeadUpdateTime.setText(getResources().getString(R.string.refresh_last_time) + HyUtil.getNowTime());
    }

    // /**
    // * 刷新时间
    // *
    // * @param refreshTime2
    // */
    // private void setRefreshTime(Long time) {
    //
    // }

    /**
     * 结束刷新事件
     */
    public void finishRefresh() {
        MyLog.i("执行了=====finishRefresh");
        imgHeadArrow.setVisibility(View.VISIBLE);
        proHead.setVisibility(View.GONE);
        txtHeadUpdateTime.setText(getResources().getString(R.string.refresh_last_time) + HyUtil.getNowTime());
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.headView.getLayoutParams();
        int i = lp.topMargin;
        scroller.startScroll(0, i, 0, headMarginTop);
        invalidate();
        isRefreshing = false;
    }

    /*
     * 该方法一般和ontouchEvent 一起用 (non-Javadoc)
     * 
     * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        int action = e.getAction();
        int y = (int) e.getRawY();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            lastY = y;
            break;

        case MotionEvent.ACTION_MOVE:
            // y移动坐标
            int m = y - lastY;

            // 记录下此刻y坐标
            this.lastY = y;
            if (m > 6 && canScroll()) {
                return true;
            }
            break;
        case MotionEvent.ACTION_UP:

            break;

        case MotionEvent.ACTION_CANCEL:

            break;
        }
        return false;
    }

    private boolean canScroll() {

        View childView;
        if (getChildCount() > 1) {
            childView = this.getChildAt(1);
            if (childView instanceof ListView) {
                int top = ((ListView) childView).getChildAt(0).getTop();
                int pad = ((ListView) childView).getListPaddingTop();
                if ((Math.abs(top - pad)) < 3 && ((ListView) childView).getFirstVisiblePosition() == 0) {
                    return true;
                } else {
                    return false;
                }
            } else if (childView instanceof ScrollView) {
                if (((ScrollView) childView).getScrollY() == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        return false;
    }

    /**
     * 刷新监听接口
     */
    public interface RefreshListener {
        public void onRefresh(RefreshLinearLayout view);
    }

}

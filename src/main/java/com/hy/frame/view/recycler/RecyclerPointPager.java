package com.hy.frame.view.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hy.frame.R;
import com.hy.frame.adapter.BaseRecyclerAdapter;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;
import com.hy.frame.view.CircleImageView;

/**
 * com.hy.frame.view.recycler
 * author HeYan
 * time 2016/8/16 14:21
 */
public class RecyclerPointPager extends RelativeLayout implements Runnable, RecyclerViewPager.OnPageChangedListener {
    private final static int DEFAULT_INTERVAL = 3000;// 间隔时间3秒
    private boolean isOpenAuto;
    private long timer;// 间隔时间
    private int scrollCount;// 次数
    private RecyclerViewPager rcyList;
    private LinearLayout llyPoint;
    //private BitmapUtils fb;
    private BaseRecyclerAdapter adapter;
    //private boolean isDrag;
    private boolean init;

    public RecyclerPointPager(Context context) {
        this(context, null);
    }

    public RecyclerPointPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerPointPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (init) return;
        init = true;
        rcyList = new RecyclerViewPager(context);
        //rcyList.setOnPageChangeListener(this);
        LayoutParams rlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rlp.alignWithParent = true;
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        addView(rcyList, rlp);
        // vPager.addView(llyContainer, llp);
        llyPoint = new LinearLayout(context);
        LayoutParams prlp = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.gallery_point_height));
        prlp.alignWithParent = true;
        prlp.addRule(RelativeLayout.ALIGN_BOTTOM);
        llyPoint.setGravity(Gravity.CENTER);
        int padding = HyUtil.dip2px(context, 2);
        llyPoint.setPadding(padding, padding, padding, padding);
        addView(llyPoint, prlp);
        rcyList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rcyList.setFlingFactor(0);
        rcyList.addOnPageChangedListener(this);
    }

    public void setAdapter(BaseRecyclerAdapter adapter) {
        if (this.adapter != null) {
            return;
        }
        this.adapter = adapter;
        llyPoint.removeAllViews();
        int size = this.adapter.getItemCount();
        for (int i = 0; i < size; i++) {
            addPoint();
        }
        rcyList.setAdapter(this.adapter);

    }

    public void resetPoint() {
        if (this.adapter == null) {
            return;
        }
        llyPoint.removeAllViews();
        int size = this.adapter.getItemCount();
        for (int i = 0; i < size; i++) {
            addPoint();
        }
    }

    private int pointResId;

    public void setPointResId(int pointResId) {
        this.pointResId = pointResId;
    }

    private void addPoint() {
        CircleImageView img = new CircleImageView(getContext());
        int width = HyUtil.dip2px(getContext(), 8);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(width, width);
        if (pointResId != 0)
            img.setBackgroundResource(pointResId);
        else
            img.setBackgroundResource(R.drawable.btn_circle_selector);
        int padding = HyUtil.dip2px(getContext(), 4);
        llp.setMargins(padding, padding, padding, padding);
        if (llyPoint.getChildCount() == 0) {
            img.setSelected(true);
        }
        llyPoint.addView(img, llp);
    }

    public void setPointGravity(int gravity) {
        if (llyPoint != null)
            llyPoint.setGravity(gravity);
    }

    /**
     * 隐藏后不再显示
     */
    public void hidePoint() {
        if (llyPoint != null)
            llyPoint.setVisibility(View.GONE);
    }

    public LinearLayout getLlyPoint() {
        return llyPoint;
    }

    private int getCount() {
        return adapter == null ? 0 : adapter.getItemCount();
    }

//    @Override
//    public void onPageScrollStateChanged(int state) {
//        if (state == ViewPager.SCROLL_STATE_DRAGGING)
//            isDrag = true;
//        if (state == ViewPager.SCROLL_STATE_IDLE)
//            isDrag = false;
//        //MyLog.e("onPageScrollStateChanged " + state);
//    }

//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        //MyLog.e("onPageScrolled " + position + "|" + positionOffset + "|" + positionOffsetPixels);
//    }

    public int getPostion() {
        if (rcyList != null) {
            return rcyList.getCurrentPosition();
        }
        return 0;
    }

    public void onPageSelected(int position) {
        boolean isDrag = false;
        int size = llyPoint.getChildCount();
        for (int i = 0; i < size; i++) {
            View v = llyPoint.getChildAt(i);
            if (position == i)
                v.setSelected(true);
            else
                v.setSelected(false);
        }
        if (scrollCount >= 3)
            scrollCount = 2;
        if (listener != null) listener.onViewChange(adapter.getItemCount(), position + 1);
    }

    /**
     * 开启倒计时
     */
    public void startAuto() {
        startAuto(DEFAULT_INTERVAL);
    }

    /**
     * 开启倒计时
     *
     * @param interval 倒计时时间(秒)
     */
    public void startAuto(int interval) {
        if (adapter == null) {
            MyLog.e("NO CALLED SHOW!");
            return;
        }
        // 防止重复开启
        if (isOpenAuto)
            return;
        isOpenAuto = true;
        timer = interval;
        run();
    }

    public void closeAuto() {
        isOpenAuto = false;
    }

    @Override
    public void run() {
        // MyLog.e("isActivated:"+isActivated());
        // MyLog.e("isAttachedToWindow:"+isAttachedToWindow());
        // MyLog.e("isTransitionGroup:"+isTransitionGroup());
        // MyLog.e("isShown:" + isShown());
        // MyLog.e("isEnabled:" + isEnabled());
        // MyLog.e("isOpaque:" + isOpaque());
        // MyLog.e("isHovered:"+isHovered());
        // MyLog.e("isInLayout:"+isInLayout());
        scrollCount++;
        postDelayed(this, timer);
        if (scrollCount < 3)
            return;
        if (!isOpenAuto || adapter == null)
            return;
        if (!isShown() || getCount() <= 1)
            return;
        int pager = rcyList.getCurrentPosition();
        if (rcyList.getScrollState() != RecyclerView.SCROLL_STATE_IDLE)
            return;
        //MyLog.e("pager:" + pager + "| getCount:" + getCount());
        if (pager < getCount() - 1)
            rcyList.smoothScrollToPosition(pager + 1);
        else
            rcyList.smoothScrollToPosition(0);

    }

    private IScrollListener listener;

    public void setListener(IScrollListener listener) {
        this.listener = listener;
    }

    @Override
    public void OnPageChanged(int oldPosition, int newPosition) {
        int size = llyPoint.getChildCount();
        for (int i = 0; i < size; i++) {
            llyPoint.getChildAt(i).setSelected(i == newPosition);
        }
        if (listener != null) {
            listener.onViewChange(adapter.getItemCount(), newPosition);
        }
    }

    public interface IScrollListener {
        void onViewChange(int all, int position);
    }
}

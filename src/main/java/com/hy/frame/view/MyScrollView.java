package com.hy.frame.view;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalBitmap;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hy.frame.R;
import com.hy.frame.adapter.ViewPagerAdapter;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;

/**
 * 显示ViewPager功能
 * 
 * @author HeYan
 * @time 2014年9月4日 下午2:37:10
 */
public class MyScrollView extends RelativeLayout implements OnPageChangeListener, Runnable{
    private final static int DEFAULT_INTERVAL = 3000;// 间隔时间3秒
    private boolean isOpenAuto;
    private long timer;// 间隔时间
    private int scrollCount;// 次数
    private ViewPager vPager;
    private LinearLayout llyPoint;
    private FinalBitmap fb;
    private List<View> views;
    private ViewPagerAdapter adapter;
    private boolean isDrag;

    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyScrollView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        vPager = new ViewPager(context);
        vPager.setOnPageChangeListener(this);
        LayoutParams rlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rlp.alignWithParent = true;
        rlp.addRule(RelativeLayout.ALIGN_TOP);
        addView(vPager, rlp);
        // vPager.addView(llyContainer, llp);
        llyPoint = new LinearLayout(context);
        LayoutParams prlp = new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.gallery_point_height));
        prlp.alignWithParent = true;
        prlp.addRule(RelativeLayout.ALIGN_BOTTOM);
        llyPoint.setGravity(Gravity.CENTER);
        llyPoint.setPadding(HyUtil.dip2px(context, 2), HyUtil.dip2px(context, 2), HyUtil.dip2px(context, 2), HyUtil.dip2px(context, 2));
        addView(llyPoint, prlp);
    }

    public void addImage(int drawId) {
        ImageView img = new ImageView(getContext());
        img.setScaleType(ScaleType.FIT_XY);
        img.setImageResource(drawId);
        img.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        img.setOnClickListener(clickListener);
        addPage(img);

    }

    public void addImage(String path) {
        if (path == null)
            return;
        if (fb == null)
            fb = FinalBitmap.create(getContext());
        ImageView img = new ImageView(getContext());
        img.setScaleType(ScaleType.FIT_XY);
        img.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        fb.display(img, path);
        img.setOnClickListener(clickListener);
        addPage(img);
    }
    private OnClickListener clickListener;
    @Override
    public void setOnClickListener(OnClickListener l) {
        this.clickListener = l;
    }

    /**
     * 添加子Page 显示时需要调用show
     * 
     * @param v
     */
    public void addPage(View v) {
        if (views == null)
            views = new ArrayList<View>();
        views.add(v);
        addPoint();
    }

    public void show() {
        show(views);
    }

    public void show(List<View> views) {
        this.views = views;
        if (adapter == null) {
            adapter = new ViewPagerAdapter(views);
            vPager.setAdapter(adapter);
        } else
            adapter.refresh(views);
    }

    private void addPoint() {
        CircleImageView img = new CircleImageView(getContext());
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(HyUtil.dip2px(getContext(), 8), HyUtil.dip2px(getContext(), 8));
        img.setBackgroundResource(R.drawable.btn_circle_selector);
        llp.setMargins(HyUtil.dip2px(getContext(), 4), HyUtil.dip2px(getContext(), 4), HyUtil.dip2px(getContext(), 4), HyUtil.dip2px(getContext(), 4));
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

    private int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING)
            isDrag = true;
        if (state == ViewPager.SCROLL_STATE_IDLE)
            isDrag = false;
        //MyLog.e("onPageScrollStateChanged " + state);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //MyLog.e("onPageScrolled " + position + "|" + positionOffset + "|" + positionOffsetPixels);
    }
    public int getPostion(){
        if(vPager!=null){
            return vPager.getCurrentItem();
        }
        return 0;
    }
    @Override
    public void onPageSelected(int positon) {
        isDrag = false;
        int size = llyPoint.getChildCount();
        for (int i = 0; i < size; i++) {
            View v = llyPoint.getChildAt(i);
            if (positon == i)
                v.setSelected(true);
            else
                v.setSelected(false);
        }
        if (scrollCount >= 3)
            scrollCount = 2;
    }

    /** 开启倒计时 */
    public void startAuto() {
        startAuto(DEFAULT_INTERVAL);
    }

    /**
     * 开启倒计时
     * 
     * @param interval
     *            倒计时时间(秒)
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
        if (isDrag)
            return;
        if (scrollCount < 3)
            return;
        if (!isOpenAuto || vPager == null)
            return;
        if (!isShown() || getCount() <= 1)
            return;
        int pager = vPager.getCurrentItem();
        //MyLog.e("pager:" + pager + "| getCount:" + getCount());
        if (pager < getCount() - 1)
            vPager.setCurrentItem(pager + 1);
        else
            vPager.setCurrentItem(0);
    }
}
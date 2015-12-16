package com.hy.frame.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.util.HyUtil;

public class MyWheelView extends ScrollView {
    public static final String TAG = MyWheelView.class.getSimpleName();

    public static class OnWheelViewListener {
        public void onSelected(int selectedIndex, String item) {
        }
    }

    private Context context;
    // private ScrollView scrollView;

    private LinearLayout views;

    public MyWheelView(Context context) {
        super(context);
        init(context);
    }

    public MyWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    // String[] items;
    List<String> items;
    private int count;

    private List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
        if (null == list)
            return;
        items = list;
        count = items.size();
        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        initData();
    }

    public static final int OFF_SET_DEFAULT = 2;
    int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    int displayItemCount; // 每页显示的数量

    int selectedIndex = 1;

    private void init(Context context) {
        this.context = context;

        // scrollView = ((ScrollView)this.getParent());
        // Logger.d(TAG, "scrollview: " + scrollView);
        // Logger.d(TAG, "parent: " + this.getParent());
        // this.setOrientation(VERTICAL);
        this.setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        views.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(views);

        scrollerTask = new Runnable() {

            public void run() {

                int newY = getScrollY();
                if (initialY - newY == 0) { // stopped
                    final int remainder = initialY % itemHeight;
                    final int divided = initialY / itemHeight;
                    // Logger.d(TAG, "initialY: " + initialY);
                    // Logger.d(TAG, "remainder: " + remainder + ", divided: " + divided);
                    if (remainder == 0) {
                        selectedIndex = divided + offset;
                        onSeletedCallBack();
                    } else {
                        if (remainder > itemHeight / 2) {
                            MyWheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyWheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                                    selectedIndex = divided + offset + 1;
                                    onSeletedCallBack();
                                }
                            });
                        } else {
                            MyWheelView.this.post(new Runnable() {
                                @Override
                                public void run() {
                                    MyWheelView.this.smoothScrollTo(0, initialY - remainder);
                                    selectedIndex = divided + offset;
                                    onSeletedCallBack();
                                }
                            });
                        }

                    }

                } else {
                    initialY = getScrollY();
                    MyWheelView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };

    }

    int initialY;

    Runnable scrollerTask;
    int newCheck = 50;

    public void startScrollerTask() {

        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;
        views.removeAllViews();
        for (String item : items) {
            views.addView(createView(item));
        }
        refreshItemView(0);
        int height = itemHeight * displayItemCount;
        this.getLayoutParams().height = height;
    }

    int itemHeight = 0;

    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        int padding = getContext().getResources().getDimensionPixelSize(R.dimen.margin_normal);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
            // itemHeight = tv.getMeasuredHeight();
            itemHeight = HyUtil.getViewMeasuredHeight(tv);
            // Logger.d(TAG, "itemHeight: " + itemHeight);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            // this.setLayoutParams(new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
            // itemHeight * displayItemCount));
        }
        return tv;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        // Logger.d(TAG, "l: " + l + ", t: " + t + ", oldl: " + oldl + ", oldt: " + oldt);

        // try {
        // Field field = ScrollView.class.getDeclaredField("mScroller");
        // field.setAccessible(true);
        // OverScroller mScroller = (OverScroller) field.get(this);
        //
        //
        // if(mScroller.isFinished()){
        // Logger.d(TAG, "isFinished...");
        // }
        //
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        refreshItemView(t);

        if (t > oldt) {
            // Logger.d(TAG, "向下滚动");
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
            // Logger.d(TAG, "向上滚动");
            scrollDirection = SCROLL_DIRECTION_UP;

        }

    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }

            // if(remainder > itemHeight / 2){
            // if(scrollDirection == SCROLL_DIRECTION_DOWN){
            // position = divided + offset;
            // Logger.d(TAG, ">down...position: " + position);
            // }else if(scrollDirection == SCROLL_DIRECTION_UP){
            // position = divided + offset + 1;
            // Logger.d(TAG, ">up...position: " + position);
            // }
            // }else{
            // // position = y / itemHeight + offset;
            // if(scrollDirection == SCROLL_DIRECTION_DOWN){
            // position = divided + offset;
            // Logger.d(TAG, "<down...position: " + position);
            // }else if(scrollDirection == SCROLL_DIRECTION_UP){
            // position = divided + offset + 1;
            // Logger.d(TAG, "<up...position: " + position);
            // }
            // }
            // }

            // if(scrollDirection == SCROLL_DIRECTION_DOWN){
            // position = divided + offset;
            // }else if(scrollDirection == SCROLL_DIRECTION_UP){
            // position = divided + offset + 1;
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            if (position == i) {
                itemView.setTextColor(Color.parseColor("#454545"));
            } else {
                itemView.setTextColor(Color.parseColor("#787878"));
            }
        }
    }

    /**
     * 获取选中区域的边界
     */
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }

    private int scrollDirection = -1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;

    Paint paint;
    int viewWidth;
    private int lineColor;

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {

        if (viewWidth == 0) {
            viewWidth = (int) context.getResources().getDisplayMetrics().xdpi;
            Log.d(TAG, "viewWidth: " + viewWidth);
        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(lineColor == 0 ? getResources().getColor(R.color.txt_gray) : lineColor);
            paint.setStrokeWidth(HyUtil.dip2px(context, 1f));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawLine(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(0, obtainSelectedAreaBorder()[1], viewWidth, obtainSelectedAreaBorder()[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        };

        super.setBackgroundDrawable(background);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Logger.d(TAG, "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    /**
     * 选中回调
     */
    private void onSeletedCallBack() {
        if (null != onWheelViewListener) {
            if (selectedIndex >= items.size())
                return;
            if (items.get(selectedIndex).length() < 1) {
                int size = selectedIndex + 1;
                final int lines = count + offset;
                if (size > count + offset) {
                    MyWheelView.this.smoothScrollTo(0, itemHeight * (lines - 1));
                    selectedIndex = lines - 1;
                    onSeletedCallBack();
                }
                return;
            }
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }

    }

    public void setSeletion(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(new Runnable() {
            @Override
            public void run() {
                MyWheelView.this.smoothScrollTo(0, p * itemHeight);
            }
        });

    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSeletedIndex() {
        return selectedIndex - offset;
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {

            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    private OnWheelViewListener onWheelViewListener;

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

}

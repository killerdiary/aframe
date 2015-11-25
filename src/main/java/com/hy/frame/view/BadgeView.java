package com.hy.frame.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TabWidget;
import android.widget.TextView;

public class BadgeView extends TextView {
    private boolean mHideOnNull;

    public BadgeView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BadgeView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842884);
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHideOnNull = true;
        this.init();
    }

    private void init() {
        if (!(this.getLayoutParams() instanceof LayoutParams)) {
            LayoutParams layoutParams = new LayoutParams(-2, -2, 53);
            this.setLayoutParams(layoutParams);
        }

        this.setTextColor(-1);
        this.setTypeface(Typeface.DEFAULT_BOLD);
        this.setTextSize(2, 11.0F);
        this.setPadding(this.dip2Px(5.0F), this.dip2Px(1.0F), this.dip2Px(5.0F), this.dip2Px(1.0F));
        this.setBackground(9, Color.parseColor("#d3321b"));
        this.setGravity(17);
        this.setHideOnNull(true);
        this.setBadgeCount(0);
    }

    public void setBackground(int dipRadius, int badgeColor) {
        int radius = this.dip2Px((float) dipRadius);
        float[] radiusArray = new float[]{(float) radius, (float) radius, (float) radius, (float) radius, (float) radius, (float) radius, (float) radius, (float) radius};
        RoundRectShape roundRect = new RoundRectShape(radiusArray, (RectF) null, (float[]) null);
        ShapeDrawable bgDrawable = new ShapeDrawable(roundRect);
        bgDrawable.getPaint().setColor(badgeColor);
        this.setBackgroundDrawable(bgDrawable);
    }

    public boolean isHideOnNull() {
        return this.mHideOnNull;
    }

    public void setHideOnNull(boolean hideOnNull) {
        this.mHideOnNull = hideOnNull;
        this.setText(this.getText());
    }

    public void setText(CharSequence text, BufferType type) {
        if (!this.isHideOnNull() || text != null && !text.toString().equalsIgnoreCase("0")) {
            this.setVisibility(0);
        } else {
            this.setVisibility(8);
        }

        super.setText(text, type);
    }

    public void setBadgeCount(int count) {
        this.setText(String.valueOf(count));
    }

    public Integer getBadgeCount() {
        if (this.getText() == null) {
            return null;
        } else {
            String text = this.getText().toString();

            try {
                return Integer.valueOf(Integer.parseInt(text));
            } catch (NumberFormatException var3) {
                return null;
            }
        }
    }

    public void setBadgeGravity(int gravity) {
        LayoutParams params = (LayoutParams) this.getLayoutParams();
        params.gravity = gravity;
        this.setLayoutParams(params);
    }

    public int getBadgeGravity() {
        LayoutParams params = (LayoutParams) this.getLayoutParams();
        return params.gravity;
    }

    public void setBadgeMargin(int dipMargin) {
        this.setBadgeMargin(dipMargin, dipMargin, dipMargin, dipMargin);
    }

    public void setBadgeMargin(int leftDipMargin, int topDipMargin, int rightDipMargin, int bottomDipMargin) {
        LayoutParams params = (LayoutParams) this.getLayoutParams();
        params.leftMargin = this.dip2Px((float) leftDipMargin);
        params.topMargin = this.dip2Px((float) topDipMargin);
        params.rightMargin = this.dip2Px((float) rightDipMargin);
        params.bottomMargin = this.dip2Px((float) bottomDipMargin);
        this.setLayoutParams(params);
    }

    public int[] getBadgeMargin() {
        LayoutParams params = (LayoutParams) this.getLayoutParams();
        return new int[]{params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin};
    }

    public void incrementBadgeCount(int increment) {
        Integer count = this.getBadgeCount();
        if (count == null) {
            this.setBadgeCount(increment);
        } else {
            this.setBadgeCount(increment + count.intValue());
        }

    }

    public void decrementBadgeCount(int decrement) {
        this.incrementBadgeCount(-decrement);
    }

    public void setTargetView(TabWidget target, int tabIndex) {
        View tabView = target.getChildTabViewAt(tabIndex);
        this.setTargetView(tabView);
    }

    public void setTargetView(View target) {
        if (this.getParent() != null) {
            ((ViewGroup) this.getParent()).removeView(this);
        }

        if (target != null) {
            if (target.getParent() instanceof FrameLayout) {
                ((FrameLayout) target.getParent()).addView(this);
            } else if (target.getParent() instanceof ViewGroup) {
                ViewGroup parentContainer = (ViewGroup) target.getParent();
                int groupIndex = parentContainer.indexOfChild(target);
                parentContainer.removeView(target);
                FrameLayout badgeContainer = new FrameLayout(this.getContext());
                android.view.ViewGroup.LayoutParams parentlayoutParams = target.getLayoutParams();
                parentContainer.addView(badgeContainer, groupIndex, parentlayoutParams);
                badgeContainer.addView(target);
                badgeContainer.addView(this);
            } else if (target.getParent() == null) {
                Log.e(this.getClass().getSimpleName(), "ParentView is needed");
            }

        }
    }

    private int dip2Px(float dip) {
        return (int) (dip * this.getContext().getResources().getDisplayMetrics().density + 0.5F);
    }
}

package com.hy.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.hy.frame.R;

/**
 * BadgeView 数字角标
 *
 * @author HeYan
 * @time 2017/5/9 9:51
 */
public class BadgeTextView extends android.support.v7.widget.AppCompatTextView {
    private boolean zeroHide;
    private int maxNubmer;
    private int badgeNumber;

    public BadgeTextView(Context context) {
        this(context, null);
    }

    public BadgeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public BadgeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BadgeTextView, defStyleAttr, 0);
        int backgrounColor = a.getColor(R.styleable.BadgeTextView_badgeBackground, context.getResources().getColor(R.color.badge_bg));
        maxNubmer = a.getInt(R.styleable.BadgeTextView_badgeMaxNubmer, 99);
        int number = a.getInt(R.styleable.BadgeTextView_badgeNubmer, 0);
        int radius = a.getDimensionPixelSize(R.styleable.BadgeTextView_badgeRadius, dp2Px(9));
        zeroHide = a.getBoolean(R.styleable.BadgeTextView_badgeZeroHide, true);
        a.recycle();
        //this.setTextColor(Color.WHITE);
        //this.setTypeface(Typeface.DEFAULT_BOLD);
        //this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11.0F);
        if (getPaddingLeft() == 0)
            setPadding(dp2Px(5), dp2Px(1), dp2Px(5), dp2Px(1));
//        if (getTextSize() > (radius * 2 - dp2Px(1) * 2))
//            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11F);
        setMinWidth(radius * 2);
        setMinHeight(radius * 2);
        setBackground(radius, backgrounColor);
        setGravity(Gravity.CENTER);
        setBadgeNumber(number);
    }

    public void setBackground(int radius, int badgeColor) {
        float[] radiusArray = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        RoundRectShape roundRect = new RoundRectShape(radiusArray, null, null);
        ShapeDrawable bgDrawable = new ShapeDrawable(roundRect);
        bgDrawable.getPaint().setColor(badgeColor);
        setBackgroundDrawable(bgDrawable);
    }

    public void setBadgeNumber(int number) {
        this.badgeNumber = number;
        if (zeroHide && number == 0) {
            setVisibility(GONE);
            return;
        }
        if (number > maxNubmer)
            setText(getResources().getString(R.string.badge_overflow, maxNubmer));
        else
            setText(String.valueOf(number));
    }

    private int dp2Px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

}

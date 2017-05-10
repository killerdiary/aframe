package com.hy.frame.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hy.frame.R;

/**
 * 可以着色的ImageView
 *
 * @author HeYan
 * @time 2017/5/9 9:52
 */
public class TintImageView extends android.support.v7.widget.AppCompatImageView {
    private ColorStateList tint;

    public TintImageView(Context context) {
        this(context, null);
    }

    //this is the constructor that causes the exception
    public TintImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    //here, obtainStyledAttributes was asking for an array
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TintImageView, defStyleAttr, 0);
        tint = a.getColorStateList(R.styleable.TintImageView_tint);
        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
//        if (tint != null && tint.isStateful())
        if (tint != null)
            updateTintColor();
    }

    public void setColorFilter(ColorStateList tint) {
        this.tint = tint;
        super.setColorFilter(tint.getColorForState(getDrawableState(), 0));
    }

    private void updateTintColor() {
        int color = tint.getColorForState(getDrawableState(), 0);
        setColorFilter(color);
    }
}
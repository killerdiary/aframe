package com.hy.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.hy.frame.R;

/**
 * 带自定义Draw宽高
 *
 * @author HeYan
 * @time 2017/4/11 11:29
 */
public class MyTextView extends AppCompatEditText {
    private boolean isEye, isShowPassword;
    private Drawable drawRight;
    //private int drawRightWidth;

    public MyTextView(Context context) {
        this(context, null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyTextView, 0, 0);
        if (a == null) return;
        int drawLeftWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawLeftWidth, 0);
        int drawTopWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawTopWidth, 0);
        int drawRightWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawRightWidth, 0);
        int drawBottomWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawBottomWidth, 0);
        if (drawLeftWidth > 0 && getCompoundDrawables()[0] != null)
            getCompoundDrawables()[0].setBounds(0, 0, drawLeftWidth, drawLeftWidth);
        if (drawTopWidth > 0 && getCompoundDrawables()[1] != null)
            getCompoundDrawables()[1].setBounds(0, 0, drawTopWidth, drawTopWidth);
        if (drawRightWidth > 0 && getCompoundDrawables()[2] != null)
            getCompoundDrawables()[2].setBounds(0, 0, drawRightWidth, drawRightWidth);
        if (drawBottomWidth > 0 && getCompoundDrawables()[3] != null)
            getCompoundDrawables()[3].setBounds(0, 0, drawBottomWidth, drawBottomWidth);
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], getCompoundDrawables()[2], getCompoundDrawables()[3]);
//        postInvalidate();
    }
}

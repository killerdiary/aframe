package com.hy.frame.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.hy.frame.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 带清除/眼睛
 *
 * @author HeYan
 * @time 2017/8/30 14:37
 */
public class MyEditText extends AppCompatEditText implements TextWatcher, View.OnFocusChangeListener {
    private static final int STYLE_NONE = 0x0000;
    private static final int STYLE_EYE = 0x0001;
    private static final int STYLE_CLEAR = 0x0002;
    private static final int STYLE_NORMAL = 0x0003;
    private int drawLeftWidth, drawLeftHeight, drawTopWidth, drawTopHeight, drawRightWidth, drawRightHeight, drawBottomWidth, drawBottomHeight;
    private Drawable drawRight;
    private int drawRightStyle;
    private boolean isShowPassword;

    public MyEditText(@NotNull Context context) {
        this(context, null);
    }

    public MyEditText(@NotNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
    }

    public MyEditText(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyEditText, defStyleAttr, 0);
        drawLeftWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawLeftWidth, 0);
        drawLeftHeight = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawLeftHeight, drawLeftWidth);
        drawTopWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawTopWidth, 0);
        drawTopHeight = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawTopHeight, drawTopWidth);
        drawRightWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawRightWidth, 0);
        drawRightHeight = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawRightHeight, drawRightWidth);
        drawBottomWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawBottomWidth, 0);
        drawBottomHeight = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawBottomHeight, drawBottomWidth);
        drawRight = a.getDrawable(R.styleable.MyEditText_mEditDrawRight);
        drawRightStyle = a.getInt(R.styleable.MyEditText_mEditDrawRightStyle, STYLE_NONE);
        if (drawRightStyle == STYLE_NONE || drawRight == null) {
            Drawable[] drawables = getCompoundDrawables();
            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
            return;
        }
        showDrawRight(drawRightStyle == STYLE_NORMAL || (isFocused() && getText().length() > 0));
        if (drawRightStyle == STYLE_EYE || drawRightStyle == STYLE_CLEAR) {
            addTextChangedListener(this);
            setOnFocusChangeListener(this);
        }
    }

    @Override
    public void setCompoundDrawables(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        if (drawLeftWidth > 0 && left != null)
            left.setBounds(0, 0, drawLeftWidth, drawLeftHeight);
        if (drawTopWidth > 0 && top != null)
            top.setBounds(0, 0, drawTopWidth, drawTopHeight);
        if (drawRightWidth > 0 && right != null)
            right.setBounds(0, 0, drawRightWidth, drawRightHeight);
        if (drawBottomWidth > 0 && bottom != null)
            bottom.setBounds(0, 0, drawBottomWidth, drawBottomHeight);
        super.setCompoundDrawables(left, top, right, bottom);
    }

    private void showDrawRight(boolean show) {
        Drawable drawable = show ? drawRight : null;
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], drawable, drawables[3]);
    }

    private void changePasswordStyle() {
        isShowPassword = !isShowPassword;
        setTransformationMethod(isShowPassword ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        setSelected(isShowPassword);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        showDrawRight(s.length() > 0);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        showDrawRight(hasFocus && getText().length() > 0);
    }

    private long pressTime;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isFocused() && drawRight != null && (drawRightStyle == STYLE_EYE || drawRightStyle == STYLE_CLEAR) && getCompoundDrawables()[2] != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_UP:
                    if (System.currentTimeMillis() - pressTime < 1000) {
                        //getTotalPaddingRight()图标左边缘至控件右边缘的距离
                        //getWidth() - getTotalPaddingRight()表示从最左边到图标左边缘的位置
                        //getWidth() - getPaddingRight()表示最左边到图标右边缘的位置
                        boolean isClick = event.getX() > getWidth() - getTotalPaddingRight() && event.getX() < getWidth() - getPaddingRight();
                        if (isClick) {
                            switch (drawRightStyle) {
                                case STYLE_EYE:
                                    changePasswordStyle();
                                    break;
                                case STYLE_CLEAR:
                                    setText(null);
                                    break;
                            }
                            return true;
                        }
                    }
                    break;
                default:
                    break;
            }

        }
        return super.onTouchEvent(event);
    }

    /**
     * 显示晃动动画
     */
    public void showShakeAnimation() {
        this.startAnimation(getShakeAnimation());
    }

    /**
     * 晃动动画
     */
    private Animation getShakeAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0f, 10f, 0f, 0f);
        translateAnimation.setInterpolator(new CycleInterpolator(5));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}
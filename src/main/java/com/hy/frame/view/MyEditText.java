package com.hy.frame.view;

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

/**
 * 带清除的
 *
 * @author HeYan
 * @time 2017/4/11 11:29
 */
public class MyEditText extends AppCompatEditText implements TextWatcher, View.OnFocusChangeListener {
    private boolean isEye, isShowPassword;
    private Drawable drawRight;
    //private int drawRightWidth;

    public MyEditText(Context context) {
        this(context, null);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyEditText, 0, 0);
        if (a == null) return;
        drawRight = a.getDrawable(R.styleable.MyEditText_mEditDrawRight);
        if (drawRight == null) return;
        int drawRightWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawRightWidth, 0);
        isEye = a.getBoolean(R.styleable.MyEditText_mEditEye, false);
        if (drawRightWidth == 0)
            drawRight.setBounds(0, 0, drawRight.getIntrinsicWidth(), drawRight.getIntrinsicHeight());
        else
            drawRight.setBounds(0, 0, drawRightWidth, drawRightWidth);
        showDrawRight(false);
        addTextChangedListener(this);
        setOnFocusChangeListener(this);
        if (a.getBoolean(R.styleable.MyEditText_mEditTest, false))
            showDrawRight();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (drawRight != null && getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (getCompoundDrawables()[2] != null) {
                    //getTotalPaddingRight()图标左边缘至控件右边缘的距离
                    //getWidth() - getTotalPaddingRight()表示从最左边到图标左边缘的位置
                    //getWidth() - getPaddingRight()表示最左边到图标右边缘的位置
                    boolean isClick = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));
                    if (isClick) {
                        if (isEye) {
                            changePasswordStyle();
                        } else {
                            setText(null);
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void changePasswordStyle() {
        isShowPassword = !isShowPassword;
        setTransformationMethod(isShowPassword ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        setSelected(isShowPassword);
    }

    private void showDrawRight() {
        showDrawRight(true);
    }

    private void showDrawRight(boolean show) {
        Drawable drawable = show ? drawRight : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], drawable, getCompoundDrawables()[3]);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        showDrawRight(getText().length() > 0);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        showDrawRight(hasFocus && getText().length() > 0);
    }

    /**
     * 显示晃动动画
     */
    public void showShakeAnimation() {
        this.startAnimation(getShakeAnimation(5));
    }


    /**
     * 晃动动画
     *
     * @param counts 1s 晃动多少下
     */
    private Animation getShakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }
}

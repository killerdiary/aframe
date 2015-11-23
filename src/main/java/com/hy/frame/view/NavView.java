package com.hy.frame.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hy.frame.R;

/**
 * 主页 Nav
 *
 * @author HeYan
 * @time 2015-8-17 下午1:31:27
 */
public class NavView extends LinearLayout implements Checkable {
    private TintImageView icoKey;
    private TextView txtKey;
    private boolean mChecked;

    public NavView(Context context) {
        this(context, null);
    }

    public NavView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NavView, 0, 0);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        if (a == null)
            return;
        Drawable draw = a.getDrawable(R.styleable.NavView_navDraw);
        CharSequence key = a.getText(R.styleable.NavView_navText);
        ColorStateList textColor = a.getColorStateList(R.styleable.NavView_navTextColor);
        ColorStateList drawTint = a.getColorStateList(R.styleable.NavView_navDrawTint);
        boolean checked = a.getBoolean(R.styleable.NavView_navChecked, false);
        a.recycle();
        icoKey = new TintImageView(getContext());
        LayoutParams illp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (draw != null)
            icoKey.setImageDrawable(draw);
        if (drawTint != null)
            icoKey.setColorFilter(drawTint);
        addView(icoKey, illp);

        txtKey = new TextView(getContext());
        LayoutParams tllp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tllp.topMargin = getResources().getDimensionPixelSize(R.dimen.padding_normal);
        if (key != null)
            txtKey.setText(key);
        txtKey.setGravity(Gravity.CENTER);
        if (textColor != null)
            txtKey.setTextColor(textColor);
        addView(txtKey, tllp);
        if (checked)
            setChecked(checked);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            setSelected(checked);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * 切换选择状态
     */
    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    /**
     * Interface definition for a callback to be invoked when the checked state of a NavView changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a NavView has changed.
         *
         * @param nav       The NavView whose state has changed.
         * @param isChecked The new checked state of NavView.
         */
        void onCheckedChanged(NavView nav, boolean isChecked);
    }
}
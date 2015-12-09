package com.hy.frame.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.util.HyUtil;

/**
 * 主页 Nav
 *
 * @author HeYan
 * @time 2015-8-17 下午1:31:27
 */
public class NavView extends FrameLayout implements Checkable {
    private LinearLayout llyContainer;
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
        llyContainer = new LinearLayout(context);
        llyContainer.setOrientation(LinearLayout.VERTICAL);
        llyContainer.setGravity(Gravity.CENTER);
        if (a == null)
            return;
        Drawable draw = a.getDrawable(R.styleable.NavView_navDraw);
        Drawable drawRight = a.getDrawable(R.styleable.NavView_navDrawRight);
        CharSequence key = a.getText(R.styleable.NavView_navText);
        ColorStateList textColor = a.getColorStateList(R.styleable.NavView_navTextColor);
        ColorStateList drawTint = a.getColorStateList(R.styleable.NavView_navDrawTint);
        float textSize = a.getDimension(R.styleable.NavView_navTextSize, 0);
        boolean checked = a.getBoolean(R.styleable.NavView_navChecked, false);
        boolean horizontal = a.getBoolean(R.styleable.NavView_navHorizontal, false);
        a.recycle();
        if (horizontal) {
            llyContainer.setOrientation(LinearLayout.HORIZONTAL);
            llyContainer.setGravity(Gravity.CENTER_VERTICAL);
        }
        icoKey = new TintImageView(context);
        LinearLayout.LayoutParams illp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (draw != null)
            icoKey.setImageDrawable(draw);
        if (drawTint != null)
            icoKey.setColorFilter(drawTint);
        llyContainer.addView(icoKey, illp);

        txtKey = new TextView(context);
        LinearLayout.LayoutParams tllp;
        if (horizontal) {
            tllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tllp.leftMargin = getResources().getDimensionPixelSize(R.dimen.margin_normal);
            txtKey.setGravity(Gravity.CENTER_VERTICAL);
        } else {
            tllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tllp.topMargin = getResources().getDimensionPixelSize(R.dimen.padding_normal);
            txtKey.setGravity(Gravity.CENTER);
        }
        if (key != null)
            txtKey.setText(key);
        //txtKey.setGravity(Gravity.CENTER);
        if (textColor != null)
            txtKey.setTextColor(textColor);
        if (textSize > 0)
            txtKey.setTextSize(HyUtil.floatToSpDimension(textSize, context));
        llyContainer.addView(txtKey, tllp);
        addView(llyContainer, new LayoutParams(horizontal ? LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT, horizontal ? LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT));
        if (horizontal && drawRight != null) {
            ImageView imgRight = new ImageView(getContext());
            LayoutParams rllp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (draw != null)
                imgRight.setImageDrawable(drawRight);
            rllp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            addView(imgRight, rllp);
        }
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

    public void setText(CharSequence text) {
        txtKey.setText(text);
    }

    public void setText(@StringRes int resId) {
        setText(getContext().getResources().getText(resId));
    }

    public void setImageResource(@DrawableRes int resId) {
        icoKey.setImageResource(resId);
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
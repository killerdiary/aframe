package com.hy.frame.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.util.HyUtil;

/**
 * 主页 Nav
 * author HeYan
 * time 2015/12/14 14:14
 */
public class NavView extends FrameLayout implements Checkable {
    private LinearLayout llyContainer;
    private TintImageView icoKey;
    private ImageView imgRight;
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
        boolean textRight = a.getBoolean(R.styleable.NavView_navTextRight, false);
        boolean center = a.getBoolean(R.styleable.NavView_navCenter, false);
        int padding = a.getDimensionPixelSize(R.styleable.NavView_navPadding, 0);
        int drawWidth = a.getDimensionPixelSize(R.styleable.NavView_navDrawWidth, 0);
        int drawHeight = a.getDimensionPixelSize(R.styleable.NavView_navDrawHeight, 0);
        int drawPadding = a.getDimensionPixelSize(R.styleable.NavView_navDrawPadding, 0);
        int drawRightWidth = a.getDimensionPixelSize(R.styleable.NavView_navDrawRightWidth, 0);
        int drawRightHeight = a.getDimensionPixelSize(R.styleable.NavView_navDrawRightHeight, 0);
        a.recycle();
        llyContainer = new LinearLayout(context);
        llyContainer.setOrientation(horizontal ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        llyContainer.setGravity(Gravity.CENTER);
        if (!horizontal) {
            setPadding(0, 0, 0, 0);
            llyContainer.setPadding(padding, padding, padding, padding);
        }
        icoKey = new TintImageView(context);
        if (draw != null)
            icoKey.setImageDrawable(draw);
        if (drawTint != null)
            icoKey.setColorFilter(drawTint);
        llyContainer.addView(icoKey, new LinearLayout.LayoutParams(drawWidth > 0 ? drawWidth : LinearLayout.LayoutParams.WRAP_CONTENT, drawHeight > 0 ? drawHeight : LinearLayout.LayoutParams.WRAP_CONTENT));
        txtKey = new TextView(context);
        if (key != null)
            txtKey.setText(key);
        if (textColor != null)
            txtKey.setTextColor(textColor);
        if (textSize > 0)
            txtKey.setTextSize(HyUtil.Companion.floatToSpDimension(textSize, context));
        if (horizontal && textRight) {
            LayoutParams txtFlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            txtFlp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            addView(txtKey, txtFlp);
        } else {
            LinearLayout.LayoutParams txtLlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (horizontal)
                txtLlp.leftMargin = drawPadding;
            else
                txtLlp.topMargin = drawPadding;
            txtKey.setGravity(Gravity.CENTER);
            llyContainer.addView(txtKey, txtLlp);
        }
        LayoutParams clp = new LayoutParams(horizontal ? LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT, horizontal ? LayoutParams.MATCH_PARENT : LayoutParams.MATCH_PARENT);
        if (center) {
            clp.width = LayoutParams.WRAP_CONTENT;
            clp.height = LayoutParams.WRAP_CONTENT;
            clp.gravity = Gravity.CENTER;
        }
        addView(llyContainer, clp);
        if (horizontal && drawRight != null) {
            imgRight = new ImageView(getContext());
            LayoutParams rllp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (drawRightWidth > 0) {
                if (drawRightHeight == 0)
                    drawRightHeight = drawRightWidth;
                imgRight.setScaleType(ImageView.ScaleType.FIT_XY);
                rllp.width = drawRightWidth;
                rllp.height = drawRightHeight;
            }
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

    public TintImageView getIcoKey() {
        return icoKey;
    }

    public ImageView getImgRight() {
        return imgRight;
    }

    public TextView getTxtKey() {
        return txtKey;
    }

    public LinearLayout getLlyContainer() {
        return llyContainer;
    }
}
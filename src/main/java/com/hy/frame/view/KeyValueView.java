package com.hy.frame.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hy.frame.R;

/**
 * key view 控件
 * author HeYan
 * time 2015/12/24 14:30
 */
public class KeyValueView extends LinearLayout {

    private TextView txtKey, txtValue;
    private ImageView imgRight;
    private boolean init;

    public KeyValueView(Context context) {
        this(context, null);
    }

    public KeyValueView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public KeyValueView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KeyValueView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (init) return;
        init = true;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KeyValueView, defStyleAttr, defStyleRes);
        if (a == null) return;
//        boolean edit = a.getBoolean(R.styleable.KeyValueView_kvEdit, false);
        Drawable drawRight = a.getDrawable(R.styleable.KeyValueView_kvDrawRight);
        CharSequence key = a.getText(R.styleable.KeyValueView_kvKey);
        ColorStateList keyColor = a.getColorStateList(R.styleable.KeyValueView_kvKeyColor);
        float keySize = a.getDimension(R.styleable.KeyValueView_kvKeySize, 0);
        CharSequence keySign = a.getText(R.styleable.KeyValueView_kvKeySign);
        CharSequence value = a.getText(R.styleable.KeyValueView_kvValue);
        ColorStateList valueColor = a.getColorStateList(R.styleable.KeyValueView_kvValueColor);
        float valueSize = a.getDimension(R.styleable.KeyValueView_kvValueSize, 0);
        CharSequence valueHint = a.getText(R.styleable.KeyValueView_kvValueHint);
        ColorStateList valueHintColor = a.getColorStateList(R.styleable.KeyValueView_kvValueHintColor);
        //int valueGravity = a.getInt(R.styleable.KeyValueView_kvValueGravity, 0);
        int valuePadding = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePadding, 0);
        int valuePaddingLeft = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePaddingLeft, 0);
        int valuePaddingRight = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePaddingRight, 0);
        int valuePaddingTop = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePaddingTop, 0);
        int valuePaddingBottom = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePaddingBottom, 0);
        int valueMargin = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMargin, 0);
        int valueMarginLeft = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMarginLeft, 0);
        int valueMarginRight = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMarginRight, 0);
        int valueMarginTop = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMarginTop, 0);
        int valueMarginBottom = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMarginBottom, 0);
        int drawRightWidth = a.getDimensionPixelSize(R.styleable.KeyValueView_kvDrawRightWidth, 0);
        boolean valueRight = a.getBoolean(R.styleable.KeyValueView_kvValueRight, false);
        a.recycle();
        txtKey = new TextView(context);
        if (key != null) txtKey.setText(key);
        if (keyColor != null) txtKey.setTextColor(keyColor);
        if (keySize > 0) txtKey.setTextSize(floatToSpDimension(context, keySize));
        if (keySign != null) txtKey.append(keySign);
        txtKey.setSingleLine(true);
        txtKey.setGravity(Gravity.TOP);
        //txtKey.setBackgroundResource(R.color.translucence);
        addView(txtKey, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        if (valuePaddingLeft == 0) valuePaddingLeft = valuePadding;
        if (valuePaddingRight == 0) valuePaddingRight = valuePadding;
        if (valuePaddingTop == 0) valuePaddingTop = valuePadding;
        if (valuePaddingBottom == 0) valuePaddingBottom = valuePadding;
        if (valueMarginLeft == 0) valueMarginLeft = valueMargin;
        if (valueMarginRight == 0) valueMarginRight = valueMargin;
        if (valueMarginTop == 0) valueMarginTop = valueMargin;
        if (valueMarginBottom == 0) valueMarginBottom = valueMargin;
        LayoutParams valueLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        valueLp.setMargins(valueMarginLeft, valueMarginTop, valueMarginRight, valueMarginBottom);
        if (drawRight != null) {
            valueLp.width = 0;
            valueLp.weight = 1;
        }
//        if (edit) {
//            editValue = new EditText(context);
//            if (value != null) editValue.setText(value);
//            if (valueColor != null) editValue.setTextColor(valueColor);
//            if (valueSize > 0) editValue.setTextSize(floatToSpDimension(context, valueSize));
//            //if (valueGravity > 0) editValue.setGravity(valueGravity);
//            if (valueHint != null) editValue.setHint(valueHint);
//            if (valueHintColor != null) editValue.setHintTextColor(valueHintColor);
//            editValue.setPadding(valuePaddingLeft, valuePaddingTop, valuePaddingRight, valuePaddingBottom);
//            editValue.setBackgroundResource(R.color.transparent);
//            editValue.setSingleLine(true);
//            editValue.setGravity(Gravity.CENTER_VERTICAL);
//            addView(editValue, valueLp);
//        } else {
        txtValue = new TextView(context);
        if (value != null) txtValue.setText(value);
        if (valueColor != null) txtValue.setTextColor(valueColor);
        if (valueSize > 0) txtValue.setTextSize(floatToSpDimension(context, valueSize));
        //if (valueGravity > 0) txtValue.setGravity(valueGravity);
        if (valueHint != null) txtValue.setHint(valueHint);
        if (valueHintColor != null) txtValue.setHintTextColor(valueHintColor);
        txtValue.setPadding(valuePaddingLeft, valuePaddingTop, valuePaddingRight, valuePaddingBottom);
        //txtValue.setBackgroundResource(R.color.divider_gray);
        txtValue.setSingleLine(true);
        if (valueRight)
            txtValue.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        else
            txtValue.setGravity(Gravity.CENTER_VERTICAL);
        addView(txtValue, valueLp);
//        }
        if (drawRight != null) {
            imgRight = new ImageView(context);
            imgRight.setImageDrawable(drawRight);
            if (drawRightWidth > 0) {
                imgRight.setScaleType(ImageView.ScaleType.FIT_XY);
                addView(imgRight, new LayoutParams(drawRightWidth, drawRightWidth));
            } else
                addView(imgRight, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }

    }

    /**
     * 把字体结果dimen转化成原sp值
     */
    private float floatToSpDimension(Context context, float value) {
        return value / context.getResources().getDisplayMetrics().scaledDensity;
    }

    public void setValue(@StringRes int resId) {
        setValue(getResources().getString(resId));
    }

    public void setValue(CharSequence sequence) {
        if (txtValue != null) txtValue.setText(sequence);
    }

    public CharSequence getValue() {
        if (txtValue != null) return txtValue.getText();
        return null;
    }

    public ImageView getImgRight() {
        return imgRight;
    }

    public TextView getTxtKey() {
        return txtKey;
    }

    public TextView getTxtValue() {
        return txtValue;
    }
}

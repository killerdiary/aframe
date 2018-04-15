package com.hy.frame.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hy.frame.R

/**
 * key view 控件
 * author HeYan
 * time 2015/12/24 14:30
 */
class KeyValueView : LinearLayout{
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr)
    }

    private var txtKey: TextView? = null
    private var txtValue: TextView? = null
    private var imgRight: ImageView? = null
    private var init: Boolean = false


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (init) return
        init = true
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.KeyValueView, defStyleAttr, 0)
                ?: return
//        boolean edit = a.getBoolean(R.styleable.KeyValueView_kvEdit, false);
        val drawRight = a.getDrawable(R.styleable.KeyValueView_kvDrawRight)
        val key = a.getText(R.styleable.KeyValueView_kvKey)
        val keyColor = a.getColorStateList(R.styleable.KeyValueView_kvKeyColor)
        val keySize = a.getDimension(R.styleable.KeyValueView_kvKeySize, 0f)
        val keySign = a.getText(R.styleable.KeyValueView_kvKeySign)
        val value = a.getText(R.styleable.KeyValueView_kvValue)
        val valueColor = a.getColorStateList(R.styleable.KeyValueView_kvValueColor)
        val valueSize = a.getDimension(R.styleable.KeyValueView_kvValueSize, 0f)
        val valueHint = a.getText(R.styleable.KeyValueView_kvValueHint)
        val valueHintColor = a.getColorStateList(R.styleable.KeyValueView_kvValueHintColor)
        //int valueGravity = a.getInt(R.styleable.KeyValueView_kvValueGravity, 0);
        val valuePadding = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePadding, 0)
        var valuePaddingLeft = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePaddingLeft, 0)
        var valuePaddingRight = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePaddingRight, 0)
        var valuePaddingTop = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePaddingTop, 0)
        var valuePaddingBottom = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValuePaddingBottom, 0)
        val valueMargin = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMargin, 0)
        var valueMarginLeft = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMarginLeft, 0)
        var valueMarginRight = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMarginRight, 0)
        var valueMarginTop = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMarginTop, 0)
        var valueMarginBottom = a.getDimensionPixelSize(R.styleable.KeyValueView_kvValueMarginBottom, 0)
        val drawRightWidth = a.getDimensionPixelSize(R.styleable.KeyValueView_kvDrawRightWidth, 0)
        val valueRight = a.getBoolean(R.styleable.KeyValueView_kvValueRight, false)
        a.recycle()
        orientation = HORIZONTAL
        txtKey = TextView(context)
        if (key != null) txtKey!!.text = key
        if (keyColor != null) txtKey!!.setTextColor(keyColor)
        if (keySize > 0) txtKey!!.textSize = floatToSpDimension(context, keySize)
        if (keySign != null) txtKey!!.append(keySign)
        txtKey!!.setSingleLine(true)
        txtKey!!.gravity = Gravity.TOP
        //txtKey.setBackgroundResource(R.color.translucence);
        addView(txtKey, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        if (valuePaddingLeft == 0) valuePaddingLeft = valuePadding
        if (valuePaddingRight == 0) valuePaddingRight = valuePadding
        if (valuePaddingTop == 0) valuePaddingTop = valuePadding
        if (valuePaddingBottom == 0) valuePaddingBottom = valuePadding
        if (valueMarginLeft == 0) valueMarginLeft = valueMargin
        if (valueMarginRight == 0) valueMarginRight = valueMargin
        if (valueMarginTop == 0) valueMarginTop = valueMargin
        if (valueMarginBottom == 0) valueMarginBottom = valueMargin
        val valueLp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        valueLp.setMargins(valueMarginLeft, valueMarginTop, valueMarginRight, valueMarginBottom)
        if (drawRight != null) {
            valueLp.width = 0
            valueLp.weight = 1f
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
        txtValue = TextView(context)
        if (value != null) txtValue!!.text = value
        if (valueColor != null) txtValue!!.setTextColor(valueColor)
        if (valueSize > 0) txtValue!!.textSize = floatToSpDimension(context, valueSize)
        //if (valueGravity > 0) txtValue.setGravity(valueGravity);
        if (valueHint != null) txtValue!!.hint = valueHint
        if (valueHintColor != null) txtValue!!.setHintTextColor(valueHintColor)
        txtValue!!.setPadding(valuePaddingLeft, valuePaddingTop, valuePaddingRight, valuePaddingBottom)
        //txtValue.setBackgroundResource(R.color.divider_gray);
        txtValue!!.setSingleLine(true)
        if (valueRight)
            txtValue!!.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
        else
            txtValue!!.gravity = Gravity.CENTER_VERTICAL
        addView(txtValue, valueLp)
        //        }
        if (drawRight != null) {
            imgRight = ImageView(context)
            imgRight!!.setImageDrawable(drawRight)
            //imgRight!!.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            if (drawRightWidth > 0) {
                imgRight!!.scaleType = ImageView.ScaleType.FIT_XY
                addView(imgRight, LinearLayout.LayoutParams(drawRightWidth, drawRightWidth))
            } else
                addView(imgRight, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }

    }

    /**
     * 把字体结果dimen转化成原sp值
     */
    private fun floatToSpDimension(context: Context, value: Float): Float {
        return value / context.resources.displayMetrics.scaledDensity
    }

    fun setValue(@StringRes resId: Int) {
        setValue(resources.getString(resId))
    }

    fun setValue(sequence: CharSequence?) {
        if (txtValue != null) txtValue!!.text = sequence
    }

    val value: CharSequence?
        get() {
            if (txtValue != null) return txtValue!!.text
            return null
        }
}

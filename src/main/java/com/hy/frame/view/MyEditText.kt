package com.hy.frame.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation

import com.hy.frame.R

/**
 * 带清除的

 * @author HeYan
 * *
 * @time 2017/4/11 11:29
 */
class MyEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle) : AppCompatEditText(context, attrs, defStyleAttr), TextWatcher, View.OnFocusChangeListener {
    private var isEye: Boolean = false
    private var isShowPassword: Boolean = false
    private var drawRight: Drawable? = null

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MyEditText, 0, 0) ?: return
        drawRight = a.getDrawable(R.styleable.MyEditText_mEditDrawRight)
        if (drawRight == null) return
        val drawRightWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawRightWidth, 0)
        isEye = a.getBoolean(R.styleable.MyEditText_mEditEye, false)
        if (drawRightWidth == 0)
            drawRight!!.setBounds(0, 0, drawRight!!.intrinsicWidth, drawRight!!.intrinsicHeight)
        else
            drawRight!!.setBounds(0, 0, drawRightWidth, drawRightWidth)
        showDrawRight(false)
        addTextChangedListener(this)
        onFocusChangeListener = this
        if (a.getBoolean(R.styleable.MyEditText_mEditTest, false))
            showDrawRight()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (drawRight != null && compoundDrawables[2] != null) {
            if (event.action == MotionEvent.ACTION_UP) {
                if (compoundDrawables[2] != null) {
                    //getTotalPaddingRight()图标左边缘至控件右边缘的距离
                    //getWidth() - getTotalPaddingRight()表示从最左边到图标左边缘的位置
                    //getWidth() - getPaddingRight()表示最左边到图标右边缘的位置
                    val isClick = event.x > width - totalPaddingRight && event.x < width - paddingRight
                    if (isClick) {
                        if (isEye) {
                            changePasswordStyle()
                        } else {
                            text = null
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }


    private fun changePasswordStyle() {
        isShowPassword = !isShowPassword
        transformationMethod = if (isShowPassword) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
        isSelected = isShowPassword
    }

    private fun showDrawRight(show: Boolean = true) {
        val drawable = if (show) drawRight else null
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], drawable, compoundDrawables[3])
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        showDrawRight(text.isNotEmpty())
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        showDrawRight(hasFocus && text.isNotEmpty())
    }

    /**
     * 显示晃动动画
     */
    fun showShakeAnimation() {
        this.startAnimation(getShakeAnimation(5))
    }


    /**
     * 晃动动画

     * @param counts 1s 晃动多少下
     */
    private fun getShakeAnimation(counts: Int): Animation {
        val translateAnimation = TranslateAnimation(0f, 10f, 0f, 0f)
        translateAnimation.interpolator = CycleInterpolator(counts.toFloat())
        translateAnimation.duration = 1000
        return translateAnimation
    }
}

package com.hy.frame.widget

import android.annotation.SuppressLint
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
 * 带清除/眼睛
 *
 * @author HeYan
 * @time 2017/8/30 14:37
 */
open class MyEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.support.v7.appcompat.R.attr.editTextStyle) : AppCompatEditText(context, attrs, defStyleAttr), TextWatcher, View.OnFocusChangeListener {
    private var drawLeftWidth: Int = 0
    private var drawLeftHeight: Int = 0
    private var drawTopWidth: Int = 0
    private var drawTopHeight: Int = 0
    private var drawRightWidth: Int = 0
    private var drawRightHeight: Int = 0
    private var drawBottomWidth: Int = 0
    private var drawBottomHeight: Int = 0
    private var drawRight: Drawable? = null
    private var drawRightStyle: Int = 0
    private var isShowPassword: Boolean = false

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MyEditText, defStyleAttr, 0)
        drawLeftWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawLeftWidth, 0)
        drawLeftHeight = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawLeftHeight, drawLeftWidth)
        drawTopWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawTopWidth, 0)
        drawTopHeight = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawTopHeight, drawTopWidth)
        drawRightWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawRightWidth, 0)
        drawRightHeight = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawRightHeight, drawRightWidth)
        drawBottomWidth = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawBottomWidth, 0)
        drawBottomHeight = a.getDimensionPixelSize(R.styleable.MyEditText_mEditDrawBottomHeight, drawBottomWidth)
        drawRight = a.getDrawable(R.styleable.MyEditText_mEditDrawRight)
        drawRightStyle = a.getInt(R.styleable.MyEditText_mEditDrawRightStyle, STYLE_NONE)
        if (drawRightStyle == STYLE_NONE || drawRight == null) {
            val drawables = compoundDrawables
            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
            return
        }
        showDrawRight(drawRightStyle == STYLE_NORMAL || isFocused && text.isNotEmpty())
        if (drawRightStyle == STYLE_EYE || drawRightStyle == STYLE_CLEAR) {
            addTextChangedListener(this)
            onFocusChangeListener = this
        }
    }

    override fun setCompoundDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        if (drawLeftWidth > 0 && left != null)
            left.setBounds(0, 0, drawLeftWidth, drawLeftHeight)
        if (drawTopWidth > 0 && top != null)
            top.setBounds(0, 0, drawTopWidth, drawTopHeight)
        if (drawRightWidth > 0 && right != null)
            right.setBounds(0, 0, drawRightWidth, drawRightHeight)
        if (drawBottomWidth > 0 && bottom != null)
            bottom.setBounds(0, 0, drawBottomWidth, drawBottomHeight)
        super.setCompoundDrawables(left, top, right, bottom)
    }

    private fun showDrawRight(show: Boolean) {
        val drawable = if (show) drawRight else null
        val drawables = compoundDrawables
        setCompoundDrawables(drawables[0], drawables[1], drawable, drawables[3])
    }

    private fun changePasswordStyle() {
        isShowPassword = !isShowPassword
        transformationMethod = if (isShowPassword) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
        isSelected = isShowPassword
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        showDrawRight(s.length > 0)
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        showDrawRight(hasFocus && text.length > 0)
    }

    private var pressTime: Long = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isFocused && drawRight != null && (drawRightStyle == STYLE_EYE || drawRightStyle == STYLE_CLEAR) && compoundDrawables[2] != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> pressTime = System.currentTimeMillis()
                MotionEvent.ACTION_UP -> if (System.currentTimeMillis() - pressTime < 1000) {
                    //getTotalPaddingRight()图标左边缘至控件右边缘的距离
                    //getWidth() - getTotalPaddingRight()表示从最左边到图标左边缘的位置
                    //getWidth() - getPaddingRight()表示最左边到图标右边缘的位置
                    val isClick = event.x > width - totalPaddingRight && event.x < width - paddingRight
                    if (isClick) {
                        when (drawRightStyle) {
                            STYLE_EYE -> changePasswordStyle()
                            STYLE_CLEAR -> text = null
                        }
                        return true
                    }
                }
                else -> {
                }
            }

        }
        return super.onTouchEvent(event)
    }

    /**
     * 显示晃动动画
     */
    fun showShakeAnimation() {
        this.startAnimation(shakeAnimation)
    }

    /**
     * 晃动动画
     */
    private val shakeAnimation: Animation
        get() {
            val translateAnimation = TranslateAnimation(0f, 10f, 0f, 0f)
            translateAnimation.interpolator = CycleInterpolator(5f)
            translateAnimation.duration = 1000
            return translateAnimation
        }

    companion object {
        private val STYLE_NONE = 0x0000
        private val STYLE_EYE = 0x0001
        private val STYLE_CLEAR = 0x0002
        private val STYLE_NORMAL = 0x0003
    }
}
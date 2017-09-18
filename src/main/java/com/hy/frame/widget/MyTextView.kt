package com.hy.frame.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

import com.hy.frame.R

/**
 * 可控制图标大小TextView，另外只支持drawableLeft居中
 *
 * @author HeYan
 * @time 2017/8/16 13:54
 */
open class MyTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var drawLeftWidth: Int = 0
    private var drawLeftHeight: Int = 0
    private var drawTopWidth: Int = 0
    private var drawTopHeight: Int = 0
    private var drawRightWidth: Int = 0
    private var drawRightHeight: Int = 0
    private var drawBottomWidth: Int = 0
    private var drawBottomHeight: Int = 0
    private var drawCenter: Boolean = false

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MyTextView, defStyleAttr, 0)
        drawLeftWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawLeftWidth, 0)
        drawLeftHeight = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawLeftHeight, drawLeftWidth)
        drawTopWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawTopWidth, 0)
        drawTopHeight = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawTopHeight, drawTopWidth)
        drawRightWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawRightWidth, 0)
        drawRightHeight = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawRightHeight, drawRightWidth)
        drawBottomWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawBottomWidth, 0)
        drawBottomHeight = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawBottomHeight, drawBottomWidth)
        drawCenter = a.getBoolean(R.styleable.MyTextView_mTxtDrawCenter, false)
        val drawables = compoundDrawables
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
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

    override fun onDraw(canvas: Canvas) {
        val drawables = compoundDrawables
        val drawableLeft = drawables[0]
        if (drawCenter && drawableLeft != null) {
            val textWidth = paint.measureText(text.toString())
            val drawablePadding = compoundDrawablePadding
            val drawableWidth = drawLeftWidth
            val bodyWidth = textWidth + drawableWidth.toFloat() + drawablePadding.toFloat()
            canvas.translate((width - bodyWidth) / 2, 0f)
        }
        super.onDraw(canvas)
    }
}

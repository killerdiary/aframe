package com.hy.frame.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.hy.frame.R

/**
 * 带自定义Draw宽高
 * @author HeYan
 * @time 2017/4/11 11:29
 */
class MyTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var drawLeftWidth: Int = 0
    private var drawLeftHeight: Int = 0
    private var drawTopWidth: Int = 0
    private var drawTopHeight: Int = 0
    private var drawRightWidth: Int = 0
    private var drawRightHeight: Int = 0
    private var drawBottomWidth: Int = 0
    private var drawBottomHeight: Int = 0

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MyTextView, 0, 0) ?: return
        drawLeftWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawLeftWidth, 0)
        drawLeftHeight = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawLeftHeight, drawLeftWidth)
        drawTopWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawTopWidth, 0)
        drawTopHeight = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawTopHeight, drawTopWidth)
        drawRightWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawRightWidth, 0)
        drawRightHeight = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawRightHeight, drawRightWidth)
        drawBottomWidth = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawBottomWidth, 0)
        drawBottomHeight = a.getDimensionPixelSize(R.styleable.MyTextView_mTxtDrawBottomHeight, drawBottomWidth)
        //        if (drawLeftWidth > 0 && getCompoundDrawables()[0] != null)
        //            getCompoundDrawables()[0].setBounds(0, 0, drawLeftWidth, drawLeftHeight);
        //        if (drawTopWidth > 0 && getCompoundDrawables()[1] != null)
        //            getCompoundDrawables()[1].setBounds(0, 0, drawTopWidth, drawTopHeight);
        //        if (drawRightWidth > 0 && getCompoundDrawables()[2] != null)
        //            getCompoundDrawables()[2].setBounds(0, 0, drawRightWidth, drawRightHeight);
        //        if (drawBottomWidth > 0 && getCompoundDrawables()[3] != null)
        //            getCompoundDrawables()[3].setBounds(0, 0, drawBottomWidth, drawBottomHeight);
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3])
    }

    //    @Override
    //    public void setCompoundDrawablesWithIntrinsicBounds(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
    //        if (drawLeftWidth > 0 && left != null)
    //            left.setBounds(0, 0, drawLeftWidth, drawLeftHeight);
    //        if (drawTopWidth > 0 && top != null)
    //            top.setBounds(0, 0, drawTopWidth, drawTopHeight);
    //        if (drawRightWidth > 0 && right != null)
    //            right.setBounds(0, 0, drawRightWidth, drawRightHeight);
    //        if (drawBottomWidth > 0 && bottom != null)
    //            bottom.setBounds(0, 0, drawBottomWidth, drawBottomHeight);
    //        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    //    }

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
}

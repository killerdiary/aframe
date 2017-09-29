package com.hy.frame.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView

class AutoFitTextView : TextView {
    private var mTextPaint: Paint? = null
    private var mTextSize: Float = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /**
     * Re size the font so the specified text fits in the text box assuming the
     * text box is the specified width.

     * @param text
     * *
     * @param textViewWidth
     */
    private fun refitText(text: String?, textViewWidth: Int) {
        if (text == null || textViewWidth <= 0)
            return
        mTextPaint = Paint()
        mTextPaint!!.set(this.paint)
        val availableTextViewWidth = width - paddingLeft - paddingRight
        val charsWidthArr = FloatArray(text.length)
        val boundsRect = Rect()
        mTextPaint!!.getTextBounds(text, 0, text.length, boundsRect)
        var textWidth = boundsRect.width()
        mTextSize = textSize
        while (textWidth > availableTextViewWidth) {
            mTextSize -= 1f
            mTextPaint!!.textSize = mTextSize
            textWidth = mTextPaint!!.getTextWidths(text, charsWidthArr)
        }
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        refitText(this.text.toString(), this.width)
    }
}  
package com.hy.frame.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

/**
 * FoucsView

 * @author HeYan
 *
 * @time 2017/4/28 15:36
 */
class FoucsView : View {
    private var foucsView_size: Int = 0
    private var x: Int = 0
    private var y: Int = 0
    private var length: Int = 0
    private var mPaint: Paint? = null

    constructor(context: Context, size: Int) : super(context) {
        foucsView_size = size
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.isDither = true
        mPaint!!.color = 0xFF00CC00.toInt()
        mPaint!!.strokeWidth = 1f
        mPaint!!.style = Paint.Style.STROKE
    }

    private constructor(context: Context) : super(context) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        y = (foucsView_size / 2.0).toInt()
        x = y
        length = (foucsView_size / 2.0).toInt() - 2
        setMeasuredDimension(foucsView_size, foucsView_size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect((x - length).toFloat(), (y - length).toFloat(), (x + length).toFloat(), (y + length).toFloat(), mPaint)
        canvas.drawLine(2f, (height / 2).toFloat(), (foucsView_size / 10).toFloat(), (height / 2).toFloat(), mPaint)
        canvas.drawLine((width - 2).toFloat(), (height / 2).toFloat(), (width - foucsView_size / 10).toFloat(), (height / 2).toFloat(), mPaint)
        canvas.drawLine((width / 2).toFloat(), 2f, (width / 2).toFloat(), (foucsView_size / 10).toFloat(), mPaint)
        canvas.drawLine((width / 2).toFloat(), (height - 2).toFloat(), (width / 2).toFloat(), (height - foucsView_size / 10).toFloat(), mPaint)
    }
}

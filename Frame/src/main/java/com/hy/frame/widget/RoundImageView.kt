package com.hy.frame.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.hy.frame.R

/**
 * 圆角ImageView 默认15度
 * @author HeYan
 * @time 2017/5/9 9:52
 */
class RoundImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : android.support.v7.widget.AppCompatImageView(context, attrs, defStyle) {

    /**
     * 圆角的大小
     */
    private var radius = 15f
    private val roundRect = RectF()
    private val maskPaint = Paint()
    private val zonePaint = Paint()

    init {
        init(context, attrs)
    }

    override fun draw(canvas: Canvas) {
        canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG)
        canvas.drawRoundRect(roundRect, radius, radius, zonePaint)
        canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG)
        super.draw(canvas)
        canvas.restore()
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.RoundImageView, 0, 0)
        radius = a.getDimensionPixelSize(R.styleable.RoundImageView_rivRadius, 0).toFloat()
        if (radius == 0f)
            radius = resources.getDimensionPixelSize(R.dimen.btn_radius).toFloat()
        a.recycle()
        maskPaint.isAntiAlias = true
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        zonePaint.isAntiAlias = true
        zonePaint.color = Color.WHITE
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val w = width
        val h = height
        roundRect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    /**
     * 设置圆角大小

     * @param radius
     */
    fun setRectAdius(radius: Float) {
        this.radius = radius
        invalidate()
    }

}

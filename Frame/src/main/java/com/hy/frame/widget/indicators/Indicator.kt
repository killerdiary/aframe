package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable

abstract class Indicator : Drawable(), Animatable {

    private val mUpdateListeners = HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener>()
    private var mAnimators: ArrayList<ValueAnimator>? = null
    private var drawBounds = ZERO_BOUNDS_RECT
    private var mHasAnimators: Boolean = false
    private val mPaint = Paint()
    private var mAlpha:Int = 255
    init {
        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    var color: Int
        get() = mPaint.color
        set(color) {
            mPaint.color = color
        }

    override fun setAlpha(alpha: Int) {
        mAlpha = alpha
    }

    override fun getAlpha(): Int {
        return mAlpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun draw(canvas: Canvas) {
        draw(canvas, mPaint)
    }

    abstract fun draw(canvas: Canvas, paint: Paint)

    abstract fun onCreateAnimators(): ArrayList<ValueAnimator>

    override fun start() {
        ensureAnimators()

        if (mAnimators == null) {
            return
        }

        // If the animators has not ended, do nothing.
        if (isStarted) {
            return
        }
        startAnimators()
        invalidateSelf()
    }

    private fun startAnimators() {
        for (i in mAnimators!!.indices) {
            val animator = mAnimators!![i]

            //when the animator restart , add the updateListener again because they
            // was removed by animator stop .
            val updateListener = mUpdateListeners[animator]
            if (updateListener != null) {
                animator.addUpdateListener(updateListener)
            }

            animator.start()
        }
    }

    private fun stopAnimators() {
        if (mAnimators != null) {
            for (animator in mAnimators!!) {
                if (animator.isStarted) {
                    animator.removeAllUpdateListeners()
                    animator.end()
                }
            }
        }
    }

    private fun ensureAnimators() {
        if (!mHasAnimators) {
            mAnimators = onCreateAnimators()
            mHasAnimators = true
        }
    }

    override fun stop() {
        stopAnimators()
    }

    private val isStarted: Boolean
        get() {
            return mAnimators!!
                    .firstOrNull()
                    ?.isStarted
                    ?: false
        }

    override fun isRunning(): Boolean {
        return mAnimators!!
                .firstOrNull()
                ?.isRunning
                ?: false
    }

    /**
     * Your should use this to add AnimatorUpdateListener when
     * create animator , otherwise , animator doesn't work when
     * the animation restart .
     * @param updateListener
     */
    fun addUpdateListener(animator: ValueAnimator, updateListener: ValueAnimator.AnimatorUpdateListener) {
        mUpdateListeners.put(animator, updateListener)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        setDrawBounds(bounds)
    }

    fun setDrawBounds(drawBounds: Rect) {
        setDrawBounds(drawBounds.left, drawBounds.top, drawBounds.right, drawBounds.bottom)
    }

    fun setDrawBounds(left: Int, top: Int, right: Int, bottom: Int) {
        this.drawBounds = Rect(left, top, right, bottom)
    }

    fun postInvalidate() {
        invalidateSelf()
    }

    fun getDrawBounds(): Rect {
        return drawBounds
    }

    val width: Int
        get() = drawBounds.width()

    val height: Int
        get() = drawBounds.height()

    fun centerX(): Int {
        return drawBounds.centerX()
    }

    fun centerY(): Int {
        return drawBounds.centerY()
    }

    fun exactCenterX(): Float {
        return drawBounds.exactCenterX()
    }

    fun exactCenterY(): Float {
        return drawBounds.exactCenterY()
    }

    companion object {
        private val ZERO_BOUNDS_RECT = Rect()
    }

}

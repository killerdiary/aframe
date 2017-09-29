package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator

open class BallScaleIndicator : Indicator() {

    protected open var scale = 1f

    override fun draw(canvas: Canvas, paint: Paint) {
        val circleSpacing = 4f
        paint.alpha = alpha
        canvas.scale(scale, scale, (width / 2).toFloat(), (height / 2).toFloat())
        paint.alpha = alpha
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), width / 2 - circleSpacing, paint)
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val scaleAnim = ValueAnimator.ofFloat(0f, 1f)
        scaleAnim.interpolator = LinearInterpolator()
        scaleAnim.duration = 1000
        scaleAnim.repeatCount = -1
        addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            scale = animation.animatedValue as Float
            postInvalidate()
        })

        val alphaAnim = ValueAnimator.ofInt(255, 0)
        alphaAnim.interpolator = LinearInterpolator()
        alphaAnim.duration = 1000
        alphaAnim.repeatCount = -1
        addUpdateListener(alphaAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            alpha = animation.animatedValue as Int
            postInvalidate()
        })
        animators.add(scaleAnim)
        animators.add(alphaAnim)
        return animators
    }

}

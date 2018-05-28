package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator

import java.util.ArrayList

class BallScaleRippleIndicator : BallScaleIndicator() {

    override fun draw(canvas: Canvas, paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        super.draw(canvas, paint)
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

        val alphaAnim = ValueAnimator.ofInt(0, 255)
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

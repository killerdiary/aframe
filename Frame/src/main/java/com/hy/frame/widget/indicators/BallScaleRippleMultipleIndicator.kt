package com.hy.frame.widget.indicators

import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator

import android.animation.ValueAnimator

import java.util.ArrayList

/**
 * Created by Jack on 2015/10/19.
 */
class BallScaleRippleMultipleIndicator : BallScaleMultipleIndicator() {


    override fun draw(canvas: Canvas, paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        super.draw(canvas, paint)
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val delays = longArrayOf(0, 200, 400)
        for (i in 0..2) {
            val scaleAnim = ValueAnimator.ofFloat(0f, 1f)
            scaleAnim.interpolator = LinearInterpolator()
            scaleAnim.duration = 1000
            scaleAnim.repeatCount = -1
            addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                scaleFloats[i] = animation.animatedValue as Float
                postInvalidate()
            })
            scaleAnim.startDelay = delays[i]

            val alphaAnim = ValueAnimator.ofInt(0, 255)
            scaleAnim.interpolator = LinearInterpolator()
            alphaAnim.duration = 1000
            alphaAnim.repeatCount = -1
            addUpdateListener(alphaAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                alphaInts[i] = animation.animatedValue as Int
                postInvalidate()
            })
            scaleAnim.startDelay = delays[i]

            animators.add(scaleAnim)
            animators.add(alphaAnim)
        }
        return animators
    }

}

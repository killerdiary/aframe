package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator

import java.util.ArrayList

open class BallZigZagIndicator : Indicator() {

    internal var translateX = FloatArray(2)
    internal var translateY = FloatArray(2)


    override fun draw(canvas: Canvas, paint: Paint) {
        for (i in 0..1) {
            canvas.save()
            canvas.translate(translateX[i], translateY[i])
            canvas.drawCircle(0f, 0f, (width / 10).toFloat(), paint)
            canvas.restore()
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val startX = (width / 6).toFloat()
        val startY = (width / 6).toFloat()
        for (i in 0..1) {
            var translateXAnim = ValueAnimator.ofFloat(startX, width - startX, width / 2f, startX)
            if (i == 1) {
                translateXAnim = ValueAnimator.ofFloat(width - startX, startX, width / 2f, width - startX)
            }
            var translateYAnim = ValueAnimator.ofFloat(startY, startY, height / 2f, startY)
            if (i == 1) {
                translateYAnim = ValueAnimator.ofFloat(height - startY, height - startY, height / 2f, height - startY)
            }

            translateXAnim.duration = 1000
            translateXAnim.interpolator = LinearInterpolator()
            translateXAnim.repeatCount = -1
            addUpdateListener(translateXAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                translateX[i] = animation.animatedValue as Float
                postInvalidate()
            })

            translateYAnim.duration = 1000
            translateYAnim.interpolator = LinearInterpolator()
            translateYAnim.repeatCount = -1
            addUpdateListener(translateYAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                translateY[i] = animation.animatedValue as Float
                postInvalidate()
            })
            animators.add(translateXAnim)
            animators.add(translateYAnim)
        }
        return animators
    }

}

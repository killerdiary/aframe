package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.view.animation.LinearInterpolator

import java.util.ArrayList

class BallTrianglePathIndicator : Indicator() {

    internal var translateX = FloatArray(3)
    internal var translateY = FloatArray(3)

    override fun draw(canvas: Canvas, paint: Paint) {
        paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE
        for (i in 0..2) {
            canvas.save()
            canvas.translate(translateX[i], translateY[i])
            canvas.drawCircle(0f, 0f, (width / 10).toFloat(), paint)
            canvas.restore()
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val startX = (width / 5).toFloat()
        val startY = (width / 5).toFloat()
        for (i in 0..2) {
            var translateXAnim = ValueAnimator.ofFloat(width / 2f, width - startX, startX, width / 2f)
            if (i == 1) {
                translateXAnim = ValueAnimator.ofFloat(width - startX, startX, width / 2f, width - startX)
            } else if (i == 2) {
                translateXAnim = ValueAnimator.ofFloat(startX, width / 2f, width - startX, startX)
            }
            var translateYAnim = ValueAnimator.ofFloat(startY, height - startY, height - startY, startY)
            if (i == 1) {
                translateYAnim = ValueAnimator.ofFloat(height - startY, height - startY, startY, height - startY)
            } else if (i == 2) {
                translateYAnim = ValueAnimator.ofFloat(height - startY, startY, height - startY, height - startY)
            }

            translateXAnim.duration = 2000
            translateXAnim.interpolator = LinearInterpolator()
            translateXAnim.repeatCount = -1
            addUpdateListener(translateXAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                translateX[i] = animation.animatedValue as Float
                postInvalidate()
            })

            translateYAnim.duration = 2000
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

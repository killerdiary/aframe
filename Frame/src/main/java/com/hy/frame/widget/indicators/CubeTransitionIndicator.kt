package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.animation.LinearInterpolator

import java.util.ArrayList

class CubeTransitionIndicator : Indicator() {

    private var translateX = FloatArray(2)
    private var translateY = FloatArray(2)
    private var degrees: Float = 0.toFloat()
    private var scaleFloat = 1.0f

    override fun draw(canvas: Canvas, paint: Paint) {
        val rWidth = (width / 5).toFloat()
        val rHeight = (height / 5).toFloat()
        for (i in 0..1) {
            canvas.save()
            canvas.translate(translateX[i], translateY[i])
            canvas.rotate(degrees)
            canvas.scale(scaleFloat, scaleFloat)
            val rectF = RectF(-rWidth / 2, -rHeight / 2, rWidth / 2, rHeight / 2)
            canvas.drawRect(rectF, paint)
            canvas.restore()
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val startX = (width / 5).toFloat()
        val startY = (height / 5).toFloat()
        for (i in 0..1) {
            translateX[i] = startX
            var translationXAnim = ValueAnimator.ofFloat(startX, width - startX, width - startX, startX, startX)
            if (i == 1) {
                translationXAnim = ValueAnimator.ofFloat(width - startX, startX, startX, width - startX, width - startX)
            }
            translationXAnim.interpolator = LinearInterpolator()
            translationXAnim.duration = 1600
            translationXAnim.repeatCount = -1
            translationXAnim.addUpdateListener { animation ->
                translateX[i] = animation.animatedValue as Float
                postInvalidate()
            }
            translateY[i] = startY
            var translationYAnim = ValueAnimator.ofFloat(startY, startY, height - startY, height - startY, startY)
            if (i == 1) {
                translationYAnim = ValueAnimator.ofFloat(height - startY, height - startY, startY, startY, height - startY)
            }
            translationYAnim.duration = 1600
            translationYAnim.interpolator = LinearInterpolator()
            translationYAnim.repeatCount = -1
            addUpdateListener(translationYAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                translateY[i] = animation.animatedValue as Float
                postInvalidate()
            })

            animators.add(translationXAnim)
            animators.add(translationYAnim)
        }

        val scaleAnim = ValueAnimator.ofFloat(1f, 0.5f, 1f, 0.5f, 1f)
        scaleAnim.duration = 1600
        scaleAnim.interpolator = LinearInterpolator()
        scaleAnim.repeatCount = -1
        addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            scaleFloat = animation.animatedValue as Float
            postInvalidate()
        })

        val rotateAnim = ValueAnimator.ofFloat(0f, 180f, 360f, 1.5f * 360, 2 * 360f)
        rotateAnim.duration = 1600
        rotateAnim.interpolator = LinearInterpolator()
        rotateAnim.repeatCount = -1
        addUpdateListener(rotateAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            degrees = animation.animatedValue as Float
            postInvalidate()
        })

        animators.add(scaleAnim)
        animators.add(rotateAnim)
        return animators
    }
}

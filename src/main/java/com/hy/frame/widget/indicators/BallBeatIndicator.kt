package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint

class BallBeatIndicator : Indicator() {

    private val scaleFloats = floatArrayOf(SCALE, SCALE, SCALE)
    private var alphas = intArrayOf(ALPHA, ALPHA, ALPHA)

    override fun draw(canvas: Canvas, paint: Paint) {
        val circleSpacing = 4f
        val radius = (width - circleSpacing * 2) / 6
        val x = width / 2 - (radius * 2 + circleSpacing)
        val y = (height / 2).toFloat()
        for (i in 0..2) {
            canvas.save()
            val translateX = x + radius * 2 * i + circleSpacing * i
            canvas.translate(translateX, y)
            canvas.scale(scaleFloats[i], scaleFloats[i])
            paint.alpha = alphas[i]
            canvas.drawCircle(0f, 0f, radius, paint)
            canvas.restore()
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val delays = intArrayOf(350, 0, 350)
        for (i in 0..2) {
            val scaleAnim = ValueAnimator.ofFloat(1f, 0.75f, 1f)
            scaleAnim.duration = 700
            scaleAnim.repeatCount = -1
            scaleAnim.startDelay = delays[i].toLong()
            addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                scaleFloats[i] = animation.animatedValue as Float
                postInvalidate()
            })
            val alphaAnim = ValueAnimator.ofInt(255, 51, 255)
            alphaAnim.duration = 700
            alphaAnim.repeatCount = -1
            alphaAnim.startDelay = delays[i].toLong()
            addUpdateListener(alphaAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                alphas[i] = animation.animatedValue as Int
                postInvalidate()
            })
            animators.add(scaleAnim)
            animators.add(alphaAnim)
        }
        return animators
    }

    companion object {

        val SCALE = 1.0f

        val ALPHA = 255
    }


}

package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint

class BallGridPulseIndicator : Indicator() {

    private var alphas = intArrayOf(ALPHA, ALPHA, ALPHA, ALPHA, ALPHA, ALPHA, ALPHA, ALPHA, ALPHA)
    private var scaleFloats = floatArrayOf(SCALE, SCALE, SCALE, SCALE, SCALE, SCALE, SCALE, SCALE, SCALE)

    override fun draw(canvas: Canvas, paint: Paint) {
        val circleSpacing = 4f
        val radius = (width - circleSpacing * 4) / 6
        val x = width / 2 - (radius * 2 + circleSpacing)
        val y = width / 2 - (radius * 2 + circleSpacing)

        for (i in 0..2) {
            for (j in 0..2) {
                canvas.save()
                val translateX = x + radius * 2 * j + circleSpacing * j
                val translateY = y + radius * 2 * i + circleSpacing * i
                canvas.translate(translateX, translateY)
                canvas.scale(scaleFloats[3 * i + j], scaleFloats[3 * i + j])
                paint.alpha = alphas[3 * i + j]
                canvas.drawCircle(0f, 0f, radius, paint)
                canvas.restore()
            }
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val durations = intArrayOf(720, 1020, 1280, 1420, 1450, 1180, 870, 1450, 1060)
        val delays = intArrayOf(-60, 250, -170, 480, 310, 30, 460, 780, 450)
        for (i in 0..8) {
            val scaleAnim = ValueAnimator.ofFloat(1f, 0.5f, 1f)
            scaleAnim.duration = durations[i].toLong()
            scaleAnim.repeatCount = -1
            scaleAnim.startDelay = delays[i].toLong()
            addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                scaleFloats[i] = animation.animatedValue as Float
                postInvalidate()
            })
            val alphaAnim = ValueAnimator.ofInt(255, 210, 122, 255)
            alphaAnim.duration = durations[i].toLong()
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

        val ALPHA = 255

        val SCALE = 1.0f
    }

}

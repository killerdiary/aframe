package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint

class BallPulseSyncIndicator : Indicator() {

    private val translateYFloats = FloatArray(3)

    override fun draw(canvas: Canvas, paint: Paint) {
        val circleSpacing = 4f
        val radius = (width - circleSpacing * 2) / 6
        val x = width / 2 - (radius * 2 + circleSpacing)
        for (i in 0..2) {
            canvas.save()
            val translateX = x + radius * 2 * i + circleSpacing * i
            canvas.translate(translateX, translateYFloats[i])
            canvas.drawCircle(0f, 0f, radius, paint)
            canvas.restore()
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val circleSpacing = 4f
        val radius = (width - circleSpacing * 2) / 6
        val delays = intArrayOf(70, 140, 210)
        for (i in 0..2) {
            val scaleAnim = ValueAnimator.ofFloat(height / 2f, height / 2 - radius * 2, height / 2f)
            scaleAnim.duration = 600
            scaleAnim.repeatCount = -1
            scaleAnim.startDelay = delays[i].toLong()
            addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                translateYFloats[i] = animation.animatedValue as Float
                postInvalidate()
            })
            animators.add(scaleAnim)
        }
        return animators
    }


}

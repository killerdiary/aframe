package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

import java.util.ArrayList

class LineScalePartyIndicator : Indicator() {

    private var scaleFloats = floatArrayOf(SCALE, SCALE, SCALE, SCALE, SCALE)

    override fun draw(canvas: Canvas, paint: Paint) {
        val translateX = (width / 9).toFloat()
        val translateY = (height / 2).toFloat()
        for (i in 0..3) {
            canvas.save()
            canvas.translate((2 + i * 2) * translateX - translateX / 2, translateY)
            canvas.scale(scaleFloats[i], scaleFloats[i])
            val rectF = RectF(-translateX / 2, -height / 2.5f, translateX / 2, height / 2.5f)
            canvas.drawRoundRect(rectF, 5f, 5f, paint)
            canvas.restore()
        }
    }


    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val durations = longArrayOf(1260, 430, 1010, 730)
        val delays = longArrayOf(770, 290, 280, 740)
        for (i in 0..3) {
            val scaleAnim = ValueAnimator.ofFloat(1f, 0.4f, 1f)
            scaleAnim.duration = durations[i]
            scaleAnim.repeatCount = -1
            scaleAnim.startDelay = delays[i]
            addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                scaleFloats[i] = animation.animatedValue as Float
                postInvalidate()
            })
            animators.add(scaleAnim)
        }
        return animators
    }

    companion object {

        val SCALE = 1.0f
    }


}

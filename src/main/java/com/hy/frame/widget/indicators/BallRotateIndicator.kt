package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint

class BallRotateIndicator : Indicator() {

    private var scaleFloat = 0.5f
    private var degress: Float = 0f

    override fun draw(canvas: Canvas, paint: Paint) {
        val radius = (width / 10).toFloat()
        val x = (width / 2).toFloat()
        val y = (height / 2).toFloat()

        canvas.rotate(degress, centerX().toFloat(), centerY().toFloat())

        canvas.save()
        canvas.translate(x - radius * 2 - radius, y)
        canvas.scale(scaleFloat, scaleFloat)
        canvas.drawCircle(0f, 0f, radius, paint)
        canvas.restore()

        canvas.save()
        canvas.translate(x, y)
        canvas.scale(scaleFloat, scaleFloat)
        canvas.drawCircle(0f, 0f, radius, paint)
        canvas.restore()

        canvas.save()
        canvas.translate(x + radius * 2 + radius, y)
        canvas.scale(scaleFloat, scaleFloat)
        canvas.drawCircle(0f, 0f, radius, paint)
        canvas.restore()
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val scaleAnim = ValueAnimator.ofFloat(0.5f, 1f, 0.5f)
        scaleAnim.duration = 1000
        scaleAnim.repeatCount = -1
        addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            scaleFloat = animation.animatedValue as Float
            postInvalidate()
        })

        val rotateAnim = ValueAnimator.ofFloat(0f, 180f, 360f)
        addUpdateListener(rotateAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            degress = animation.animatedValue as Float
            postInvalidate()
        })
        rotateAnim.duration = 1000
        rotateAnim.repeatCount = -1

        animators.add(scaleAnim)
        animators.add(rotateAnim)
        return animators
    }

}

package com.hy.frame.widget.indicators

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

import android.animation.ValueAnimator


import java.util.ArrayList

/**
 * Created by Jack on 2015/10/17.
 */
class BallClipRotateMultipleIndicator : Indicator() {

    private var scaleFloat = 1f
    private var degrees = 0f


    override fun draw(canvas: Canvas, paint: Paint) {
        paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE

        val circleSpacing = 12f
        val x = (width / 2).toFloat()
        val y = (height / 2).toFloat()

        canvas.save()

        canvas.translate(x, y)
        canvas.scale(scaleFloat, scaleFloat)
        canvas.rotate(degrees)

        //draw two big arc
        val bStartAngles = floatArrayOf(135f, -45f)
        for (i in 0..1) {
            val rectF = RectF(-x + circleSpacing, -y + circleSpacing, x - circleSpacing, y - circleSpacing)
            canvas.drawArc(rectF, bStartAngles[i], 90f, false, paint)
        }

        canvas.restore()
        canvas.translate(x, y)
        canvas.scale(scaleFloat, scaleFloat)
        canvas.rotate(-degrees)
        //draw two small arc
        val sStartAngles = floatArrayOf(225f, 45f)
        for (i in 0..1) {
            val rectF = RectF(-x / 1.8f + circleSpacing, -y / 1.8f + circleSpacing, x / 1.8f - circleSpacing, y / 1.8f - circleSpacing)
            canvas.drawArc(rectF, sStartAngles[i], 90f, false, paint)
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val scaleAnim = ValueAnimator.ofFloat(1f, 0.6f, 1f)
        scaleAnim.duration = 1000
        scaleAnim.repeatCount = -1
        addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            scaleFloat = animation.animatedValue as Float
            postInvalidate()
        })

        val rotateAnim = ValueAnimator.ofFloat(0f, 180f, 360f)
        rotateAnim.duration = 1000
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

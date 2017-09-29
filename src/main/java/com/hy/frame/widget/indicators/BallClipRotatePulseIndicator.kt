package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class BallClipRotatePulseIndicator : Indicator() {

    private var scaleFloat1: Float = 0f
    private var scaleFloat2: Float = 0f
    private var degrees: Float = 0f

    override fun draw(canvas: Canvas, paint: Paint) {
        val circleSpacing = 12f
        val x = (width / 2).toFloat()
        val y = (height / 2).toFloat()

        //draw fill circle
        canvas.save()
        canvas.translate(x, y)
        canvas.scale(scaleFloat1, scaleFloat1)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, x / 2.5f, paint)

        canvas.restore()

        canvas.translate(x, y)
        canvas.scale(scaleFloat2, scaleFloat2)
        canvas.rotate(degrees)

        paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE

        //draw two arc
        val startAngles = floatArrayOf(225f, 45f)
        for (i in 0..1) {
            val rectF = RectF(-x + circleSpacing, -y + circleSpacing, x - circleSpacing, y - circleSpacing)
            canvas.drawArc(rectF, startAngles[i], 90f, false, paint)
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val scaleAnim = ValueAnimator.ofFloat(1f, 0.3f, 1f)
        scaleAnim.duration = 1000
        scaleAnim.repeatCount = -1
        addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            scaleFloat1 = animation.animatedValue as Float
            postInvalidate()
        })

        val scaleAnim2 = ValueAnimator.ofFloat(1f, 0.6f, 1f)
        scaleAnim2.duration = 1000
        scaleAnim2.repeatCount = -1
        addUpdateListener(scaleAnim2, ValueAnimator.AnimatorUpdateListener { animation ->
            scaleFloat2 = animation.animatedValue as Float
            postInvalidate()
        })

        val rotateAnim = ValueAnimator.ofFloat(0f, 180f, 360f)
        rotateAnim.duration = 1000
        rotateAnim.repeatCount = -1
        addUpdateListener(rotateAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            degrees = animation.animatedValue as Float
            postInvalidate()
        })
        val animators = ArrayList<ValueAnimator>()
        animators.add(scaleAnim)
        animators.add(scaleAnim2)
        animators.add(rotateAnim)
        return animators
    }
}

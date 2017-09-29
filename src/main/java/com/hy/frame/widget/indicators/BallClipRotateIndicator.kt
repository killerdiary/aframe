package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class BallClipRotateIndicator : Indicator() {

    private var scaleFloat = 1f
    private var degrees: Float = 0f

    override fun draw(canvas: Canvas, paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f

        val circleSpacing = 12f
        val x = (width / 2).toFloat()
        val y = (height / 2).toFloat()
        canvas.translate(x, y)
        canvas.scale(scaleFloat, scaleFloat)
        canvas.rotate(degrees)
        val rectF = RectF(-x + circleSpacing, -y + circleSpacing, 0 + x - circleSpacing, 0 + y - circleSpacing)
        canvas.drawArc(rectF, -45f, 270f, false, paint)
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val scaleAnim = ValueAnimator.ofFloat(1f, 0.6f, 0.5f, 1f)
        scaleAnim.duration = 750
        scaleAnim.repeatCount = -1
        addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            scaleFloat = animation.animatedValue as Float
            postInvalidate()
        })
        val rotateAnim = ValueAnimator.ofFloat(0f, 180f, 360f)
        rotateAnim.duration = 750
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

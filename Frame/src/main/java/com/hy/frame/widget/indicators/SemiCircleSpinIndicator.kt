package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

import java.util.ArrayList

class SemiCircleSpinIndicator : Indicator() {

    private var degress: Float = 0.toFloat()

    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.rotate(degress, centerX().toFloat(), centerY().toFloat())
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawArc(rectF, -60f, 120f, false, paint)
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val rotateAnim = ValueAnimator.ofFloat(0f, 180f, 360f)
        addUpdateListener(rotateAnim, ValueAnimator.AnimatorUpdateListener { animation ->
            degress = animation.animatedValue as Float
            postInvalidate()
        })
        rotateAnim.duration = 600
        rotateAnim.repeatCount = -1
        animators.add(rotateAnim)
        return animators
    }

}

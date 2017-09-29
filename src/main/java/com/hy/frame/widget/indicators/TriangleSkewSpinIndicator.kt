package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.view.animation.LinearInterpolator

import java.util.ArrayList

class TriangleSkewSpinIndicator : Indicator() {

    private var rotateX: Float = 0.toFloat()
    private var rotateY: Float = 0.toFloat()

    private val mCamera: Camera = Camera()
    private val mMatrix: Matrix = Matrix()

    override fun draw(canvas: Canvas, paint: Paint) {


        mMatrix.reset()
        mCamera.save()
        mCamera.rotateX(rotateX)
        mCamera.rotateY(rotateY)
        mCamera.getMatrix(mMatrix)
        mCamera.restore()

        mMatrix.preTranslate((-centerX()).toFloat(), (-centerY()).toFloat())
        mMatrix.postTranslate(centerX().toFloat(), centerY().toFloat())
        canvas.concat(mMatrix)

        val path = Path()
        path.moveTo((width / 5).toFloat(), (height * 4 / 5).toFloat())
        path.lineTo((width * 4 / 5).toFloat(), (height * 4 / 5).toFloat())
        path.lineTo((width / 2).toFloat(), (height / 5).toFloat())
        path.close()
        canvas.drawPath(path, paint)
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val animator = ValueAnimator.ofFloat(0f, 180f, 180f, 0f, 0f)
        addUpdateListener(animator, ValueAnimator.AnimatorUpdateListener { animation ->
            rotateX = animation.animatedValue as Float
            postInvalidate()
        })
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = -1
        animator.duration = 2500

        val animator1 = ValueAnimator.ofFloat(0f, 0f, 180f, 180f, 0f)
        addUpdateListener(animator1, ValueAnimator.AnimatorUpdateListener { animation ->
            rotateY = animation.animatedValue as Float
            postInvalidate()
        })
        animator1.interpolator = LinearInterpolator()
        animator1.repeatCount = -1
        animator1.duration = 2500

        animators.add(animator)
        animators.add(animator1)
        return animators
    }

}

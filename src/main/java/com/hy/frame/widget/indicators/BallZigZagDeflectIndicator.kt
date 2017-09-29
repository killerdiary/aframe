package com.hy.frame.widget.indicators

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class BallZigZagDeflectIndicator : BallZigZagIndicator() {


    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val startX = (width / 6).toFloat()
        val startY = (width / 6).toFloat()
        for (i in 0..1) {
            var translateXAnim = ValueAnimator.ofFloat(startX, width - startX, startX, width - startX, startX)
            if (i == 1) {
                translateXAnim = ValueAnimator.ofFloat(width - startX, startX, width - startX, startX, width - startX)
            }
            var translateYAnim = ValueAnimator.ofFloat(startY, startY, height - startY, height - startY, startY)
            if (i == 1) {
                translateYAnim = ValueAnimator.ofFloat(height - startY, height - startY, startY, startY, height - startY)
            }

            translateXAnim.duration = 2000
            translateXAnim.interpolator = LinearInterpolator()
            translateXAnim.repeatCount = -1
            addUpdateListener(translateXAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                translateX[i] = animation.animatedValue as Float
                postInvalidate()
            })

            translateYAnim.duration = 2000
            translateYAnim.interpolator = LinearInterpolator()
            translateYAnim.repeatCount = -1
            addUpdateListener(translateYAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                translateY[i] = animation.animatedValue as Float
                postInvalidate()
            })

            animators.add(translateXAnim)
            animators.add(translateYAnim)
        }
        return animators
    }

}

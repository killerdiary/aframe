package com.hy.app.ui.load

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.hy.app.R
import com.hy.app.widget.load.EyelidView
import com.hy.app.widget.load.GraduallyTextView
import com.hy.frame.app.BaseDialog

/**
 * Created by HeYan on 2016/5/9.
 */
class CatLoadingDialog(context: Context) : BaseDialog(context) {
    private var operatingAnim: RotateAnimation? = null
    private var eye_left_Anim: RotateAnimation? = null
    private var eye_right_Anim: RotateAnimation? = null
    private var imgMouse: View? = null
    private var imgLeftEye: View? = null
    private var imgRightEye: View? = null
    private var evLeft: EyelidView? = null
    private var evRight: EyelidView? = null
    private var txtMsg: GraduallyTextView? = null

    override fun getLayoutId(): Int {
        return R.layout.act_loading_cat
    }

    override fun initWindow() {
        windowDeploy(0.8f, WindowManager.LayoutParams.WRAP_CONTENT.toFloat(), Gravity.CENTER)
        setCancelable(true)
    }

    override fun initView() {
        imgMouse = findViewById(R.id.loading_cat_imgMouse)
        imgLeftEye = findViewById(R.id.loading_cat_imgLeftEye)
        imgRightEye = findViewById(R.id.loading_cat_imgRightEye)
        evLeft = findViewById(R.id.loading_cat_evLeft)
        evRight = findViewById(R.id.loading_cat_evRight)
        txtMsg = findViewById(R.id.loading_cat_txtMsg)
    }

    override fun initData() {
        operatingAnim = RotateAnimation(360f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        operatingAnim?.repeatCount = Animation.INFINITE
        operatingAnim?.duration = 2000

        eye_left_Anim = RotateAnimation(360f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        eye_left_Anim?.repeatCount = Animation.INFINITE
        eye_left_Anim?.duration = 2000

        eye_right_Anim = RotateAnimation(360f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        eye_right_Anim?.repeatCount = Animation.INFINITE
        eye_right_Anim?.duration = 2000

        val interpolator = LinearInterpolator()
        operatingAnim?.interpolator = interpolator
        eye_left_Anim?.interpolator = interpolator
        eye_right_Anim?.interpolator = interpolator

        evLeft?.setColor(Color.parseColor("#d0ced1"))
        evLeft?.setFromFull(true)

        evRight?.setColor(Color.parseColor("#d0ced1"))
        evRight?.setFromFull(true)
        operatingAnim?.setAnimationListener(
                object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {}

                    override fun onAnimationRepeat(animation: Animation) {
                        evLeft?.resetAnimator()
                        evRight?.resetAnimator()
                    }
                })
        imgMouse?.animation = operatingAnim
        imgLeftEye?.animation = eye_left_Anim
        imgRightEye?.animation = eye_right_Anim
        //evRight.setAnimation(eye_right_Anim);
        evLeft?.startLoading()
        evRight?.startLoading()
        txtMsg?.startLoading()
    }

    override fun onViewClick(v: View) {}

    override fun show() {
        super.show()
    }

    override fun hide() {
        super.hide()
    }

    override fun dismiss() {
        super.dismiss()
    }
}

package com.hy.frame.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import com.hy.frame.R

/**
 * 可以着色的ImageView
 * @author HeYan
 * @time 2017/5/9 9:52
 */
class TintImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : android.support.v7.widget.AppCompatImageView(context, attrs, defStyleAttr) {
    private var tint: ColorStateList? = null

    init {
        init(context, attrs, defStyleAttr)
    }

    //here, obtainStyledAttributes was asking for an array
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TintImageView, defStyleAttr, 0)
        tint = a.getColorStateList(R.styleable.TintImageView_tint)
        a.recycle()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        //        if (tint != null && tint.isStateful())
        if (tint != null)
            updateTintColor()
    }

    fun setColorFilter(tint: ColorStateList) {
        this.tint = tint
        super.setColorFilter(tint.getColorForState(drawableState, 0))
    }

    private fun updateTintColor() {
        val color = tint!!.getColorForState(drawableState, 0)
        setColorFilter(color)
    }
}
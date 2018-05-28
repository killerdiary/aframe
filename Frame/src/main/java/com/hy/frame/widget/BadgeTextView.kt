package com.hy.frame.widget

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import com.hy.frame.R
import com.hy.frame.util.ResUtil

/**
 * BadgeView 数字角标
 * @author HeYan
 * @time 2017/5/9 9:51
 */
class BadgeTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle) : android.support.v7.widget.AppCompatTextView(context, attrs, defStyleAttr) {
    private var zeroHide: Boolean = false
    private var maxNubmer: Int = 0
    private var badgeNumber: Int = 0

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BadgeTextView, defStyleAttr, 0)
        val bgColor = a.getColor(R.styleable.BadgeTextView_badgeBackground, ResUtil.getColor(context, R.color.red))
        maxNubmer = a.getInt(R.styleable.BadgeTextView_badgeMaxNubmer, 99)
        val number = a.getInt(R.styleable.BadgeTextView_badgeNubmer, 0)
        val radius = a.getDimensionPixelSize(R.styleable.BadgeTextView_badgeRadius, ResUtil.dip2px(context, 9F))
        zeroHide = a.getBoolean(R.styleable.BadgeTextView_badgeZeroHide, true)
        a.recycle()
        if (paddingLeft == 0)
            setPadding(ResUtil.dip2px(context, 5F), ResUtil.dip2px(context, 1F), ResUtil.dip2px(context, 5F), ResUtil.dip2px(context, 1F))
        minWidth = radius * 2
        minHeight = radius * 2
        setBackground(radius, bgColor)
        gravity = Gravity.CENTER
        setBadgeNumber(number)
    }

    fun setBackground(radius: Int, badgeColor: Int) {
        val radiusArray = floatArrayOf(radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat())
        val roundRect = RoundRectShape(radiusArray, null, null)
        val bgDrawable = ShapeDrawable(roundRect)
        bgDrawable.paint.color = badgeColor
        setBackgroundDrawable(bgDrawable)
    }

    fun setBadgeNumber(number: Int) {
        this.badgeNumber = number
        if (zeroHide && number == 0) {
            visibility = View.GONE
            return
        }
        visibility = View.VISIBLE
        if (number > maxNubmer)
            text = resources.getString(R.string.badge_overflow, maxNubmer)
        else
            text = number.toString()
    }

}

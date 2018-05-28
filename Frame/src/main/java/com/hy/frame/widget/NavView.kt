package com.hy.frame.widget

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.Gravity
import android.widget.*
import com.hy.frame.R
import com.hy.frame.util.DimensionUtil
import com.hy.frame.util.HyUtil

/**
 * 主页 Nav
 * author HeYan
 * time 2015/12/14 14:14
 */
class NavView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), Checkable {
    var llyContainer: LinearLayout? = null
        private set
    var icoKey: TintImageView? = null
        private set
    var imgRight: ImageView? = null
        private set
    var txtKey: TextView? = null
        private set
    private var mChecked: Boolean = false

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.NavView, 0, 0) ?: return
        val draw = a.getDrawable(R.styleable.NavView_navDraw)
        val drawRight = a.getDrawable(R.styleable.NavView_navDrawRight)
        val key = a.getText(R.styleable.NavView_navText)
        val textColor = a.getColorStateList(R.styleable.NavView_navTextColor)
        val drawTint = a.getColorStateList(R.styleable.NavView_navDrawTint)
        val textSize = a.getDimension(R.styleable.NavView_navTextSize, 0f)
        val checked = a.getBoolean(R.styleable.NavView_navChecked, false)
        val horizontal = a.getBoolean(R.styleable.NavView_navHorizontal, false)
        val textRight = a.getBoolean(R.styleable.NavView_navTextRight, false)
        val center = a.getBoolean(R.styleable.NavView_navCenter, false)
        val padding = a.getDimensionPixelSize(R.styleable.NavView_navPadding, 0)
        val drawWidth = a.getDimensionPixelSize(R.styleable.NavView_navDrawWidth, 0)
        val drawHeight = a.getDimensionPixelSize(R.styleable.NavView_navDrawHeight, 0)
        val drawPadding = a.getDimensionPixelSize(R.styleable.NavView_navDrawPadding, 0)
        val drawRightWidth = a.getDimensionPixelSize(R.styleable.NavView_navDrawRightWidth, 0)
        var drawRightHeight = a.getDimensionPixelSize(R.styleable.NavView_navDrawRightHeight, 0)
        a.recycle()
        llyContainer = LinearLayout(context)
        llyContainer!!.orientation = if (horizontal) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
        llyContainer!!.gravity = Gravity.CENTER
        if (!horizontal) {
            setPadding(0, 0, 0, 0)
            llyContainer!!.setPadding(padding, padding, padding, padding)
        }
        icoKey = TintImageView(context)
        if (draw != null)
            icoKey!!.setImageDrawable(draw)
        if (drawTint != null)
            icoKey!!.setColorFilter(drawTint)
        llyContainer!!.addView(icoKey, LinearLayout.LayoutParams(if (drawWidth > 0) drawWidth else LinearLayout.LayoutParams.WRAP_CONTENT, if (drawHeight > 0) drawHeight else LinearLayout.LayoutParams.WRAP_CONTENT))
        txtKey = TextView(context)
        if (key != null)
            txtKey!!.text = key
        if (textColor != null)
            txtKey!!.setTextColor(textColor)
        if (textSize > 0)
            txtKey!!.textSize = DimensionUtil.px2sp(textSize, context)
        if (horizontal && textRight) {
            val txtFlp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            txtFlp.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            addView(txtKey, txtFlp)
        } else {
            val txtLlp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            if (horizontal)
                txtLlp.leftMargin = drawPadding
            else
                txtLlp.topMargin = drawPadding
            txtKey!!.gravity = Gravity.CENTER
            llyContainer!!.addView(txtKey, txtLlp)
        }
        val clp = FrameLayout.LayoutParams(if (horizontal) FrameLayout.LayoutParams.WRAP_CONTENT else FrameLayout.LayoutParams.MATCH_PARENT, if (horizontal) FrameLayout.LayoutParams.MATCH_PARENT else FrameLayout.LayoutParams.MATCH_PARENT)
        if (center) {
            clp.width = FrameLayout.LayoutParams.WRAP_CONTENT
            clp.height = FrameLayout.LayoutParams.WRAP_CONTENT
            clp.gravity = Gravity.CENTER
        }
        addView(llyContainer, clp)
        if (horizontal && drawRight != null) {
            imgRight = ImageView(getContext())
            val rllp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            if (drawRightWidth > 0) {
                if (drawRightHeight == 0)
                    drawRightHeight = drawRightWidth
                imgRight!!.scaleType = ImageView.ScaleType.FIT_XY
                rllp.width = drawRightWidth
                rllp.height = drawRightHeight
            }
            imgRight!!.setImageDrawable(drawRight)
            rllp.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            addView(imgRight, rllp)
        }
        isChecked = checked
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            isSelected = checked
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    /**
     * 切换选择状态
     */
    override fun toggle() {
        isChecked = !mChecked
    }

    fun setText(text: CharSequence?) {
        txtKey!!.text = text
    }

    fun setText(@StringRes resId: Int) {
        setText(context.resources.getText(resId))
    }

    fun setImageResource(@DrawableRes resId: Int) {
        icoKey!!.setImageResource(resId)
    }

    /**
     * Interface definition for a callback to be invoked when the checked state of a NavView changed.
     */
    interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a NavView has changed.

         * @param nav       The NavView whose state has changed.
         *
         * @param isChecked The new checked state of NavView.
         */
        fun onCheckedChanged(nav: NavView, isChecked: Boolean)
    }
}
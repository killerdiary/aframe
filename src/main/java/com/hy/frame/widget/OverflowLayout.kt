package com.hy.frame.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.hy.frame.R

/**
 * OverflowLayout
 * @author HeYan
 * @time 2017/10/20 15:29
 */
class OverflowLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private var horizontalSpacing: Int = 0
    private var verticalSpacing: Int = 0

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.OverflowLayout, defStyleAttr, 0)
        horizontalSpacing = a.getDimensionPixelSize(R.styleable.OverflowLayout_oflHorizontalSpacing, context.resources.getDimensionPixelSize(R.dimen.margin_normal))
        verticalSpacing = a.getDimensionPixelSize(R.styleable.OverflowLayout_oflVerticalSpacing, context.resources.getDimensionPixelSize(R.dimen.margin_normal))
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        //var width = 0
        var totalHeight = 0

        var row = 0 // The row counter.
        var rowWidth = 0 // Calc the current row width.
        var rowMaxHeight = 0 // Calc the max tag height, in current row.
        var childWidth = 0
        var childHeight = 0
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i) ?: continue
            if (child.visibility == View.GONE) continue
            childWidth = child.measuredWidth
            childHeight = child.measuredHeight
            rowWidth += childWidth + horizontalSpacing
            if (rowWidth > widthSize) { // Next line.
                rowWidth = childWidth + horizontalSpacing // The next row width.
                totalHeight += rowMaxHeight + verticalSpacing
                rowMaxHeight = childHeight // The next row max height.
                row++
            } else { // This line.
                rowMaxHeight = Math.max(rowMaxHeight, childHeight)
            }
        }
        // Account for the last row height.
        totalHeight += rowMaxHeight
        // Account for the padding too.
        totalHeight += paddingTop + paddingBottom
        var totalWidth = 0
        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0) {
            totalWidth = rowWidth
            totalWidth += paddingLeft + paddingRight
        } else {// If the tags grouped exceed one line, set the width to match the parent.
            totalWidth = widthSize
        }
        setMeasuredDimension(if (widthMode == View.MeasureSpec.EXACTLY) widthSize else totalWidth,
                if (heightMode == View.MeasureSpec.EXACTLY) heightSize else totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentLeft = paddingLeft
        val parentRight = r - l - paddingRight
        val parentTop = paddingTop
        //val parentBottom = b - t - paddingBottom

        var childLeft = parentLeft
        var childTop = parentTop

        var rowMaxHeight = 0

        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            val width = child.measuredWidth
            val height = child.measuredHeight
            if (child.visibility == View.GONE) continue
            if (childLeft + width > parentRight) { // Next line
                childLeft = parentLeft
                childTop += rowMaxHeight + verticalSpacing
                rowMaxHeight = height
            } else {
                rowMaxHeight = Math.max(rowMaxHeight, height)
            }
            child.layout(childLeft, childTop, childLeft + width, childTop + height)

            childLeft += width + horizontalSpacing
        }
    }

}
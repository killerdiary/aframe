package com.hy.frame.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

/**
 * MyGridView ScrollView 冲突
 * @author HeYan
 * @time 2017/5/24 14:01
 */
@Deprecated("不建议使用GridView")
class MyGridView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : GridView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}
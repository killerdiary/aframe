package com.hy.frame.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hy.frame.R
import org.jetbrains.annotations.Nullable

@SuppressLint("ViewConstructor")
/**
 * 单选多选LinearLayout，选项必须设置ID
 * @author HeYan
 * @time 2017/9/14 13:51
 */
class MyRadioGroup : LinearLayout {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private var clickListener: View.OnClickListener? = null
        get() {
            if (field == null)
                field = View.OnClickListener { v: View -> setCheckedChild(v) }
            return field
        }
    //单选
    var checkedViewId = View.NO_ID
        private set
    //多选
    var checkedViewIds: MutableSet<Int>? = null
        private set
    var isMultiChoice: Boolean = false
    var mOnCheckedChangeListener: OnCheckedChangeListener? = null

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MyRadioGroup, 0, 0) ?: return
        isMultiChoice = a.getBoolean(R.styleable.MyRadioGroup_mrgMultiChoice, false)
        a.recycle()
    }

    override fun onViewAdded(child: View) {
        if (child.id != 0 && child.id != View.NO_ID) {
            if (child.isSelected) {
                if (isMultiChoice)
                    addChildIdToCheckedViewIds(child.id)
                else
                    checkedViewId = child.id
            }
            child.setOnClickListener(clickListener)
        }
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = listener
    }

    fun setCheckedChild(v: View) {
        setCheckedChild(v.id)
    }

    fun setCheckedChild(@IdRes id: Int) {
        if (id == 0 || id == View.NO_ID) return
        checkedViewId = id
        val count = childCount
        (0 until count)
                .map { getChildAt(it) }
                .filter { it.id == 0 || it.id == View.NO_ID }
                .forEach {
                    if (isMultiChoice) {
                        if (it.id == id) {
                            it.isSelected = !it.isSelected
                            if (it.isSelected)
                                addChildIdToCheckedViewIds(it.id)
                            else
                                checkedViewIds?.remove(it.id)
                        }
                    } else {
                        it.isSelected = id == it.id
                    }
                }
    }

    private fun addChildIdToCheckedViewIds(@IdRes id: Int) {
        if (checkedViewIds == null)
            checkedViewIds = HashSet()
        checkedViewIds?.add(id)
    }

    fun setCheckedChildPosition(position: Int) {
        val count = childCount
        if (position >= count) return
        var index = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.id == 0 || child.id == View.NO_ID) {
                index++
                if (index == position + 1) {
                    setCheckedChild(child.id)
                    break
                }
            }
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(group: MyRadioGroup, child: View, @IdRes checkedId: Int)
    }
}
package com.hy.frame.ui

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.hy.frame.R
import com.hy.frame.common.BaseDialog
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.frame.widget.loopview.LoopWheelView
import java.util.*

/**
 * 选择器
 * @author HeYan
 * @time 2017/7/31 15:07
 */
class PickerDialog(context: Context) : BaseDialog(context) {

    private var lwvList: LoopWheelView? = null
    private var btnCancel: Button? = null
    private var btnConfirm: Button? = null
    var datas: MutableList<String>? = null
    var visibleNumber: Int = 0
    override fun initLayoutId(): Int {
        return R.layout.dlg_picker
    }

    override fun initWindow() {
        windowDeploy(WindowManager.LayoutParams.MATCH_PARENT.toFloat(), WindowManager.LayoutParams.WRAP_CONTENT.toFloat(), Gravity.BOTTOM)
        window!!.setWindowAnimations(R.style.animBottomInBottomOutStyle)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }

    override fun initView() {
        lwvList = findViewById(R.id.picker_lwvList)
        btnCancel = setOnClickListener(R.id.picker_btnCancel)
        btnConfirm = setOnClickListener(R.id.picker_btnConfirm)
    }

    override fun initData() {
        if (visibleNumber > 0) {
            if (visibleNumber <= 5) {
                lwvList?.layoutParams?.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120f, context.resources.displayMetrics).toInt()
            }
            lwvList?.setItemsVisibleCount(visibleNumber)
        }
        lwvList?.setItems(datas)
    }

    override fun onViewClick(v: View) {
        dismiss()
        when (v.id) {
            R.id.picker_btnConfirm -> listener?.onDlgConfirm(this)
        }
    }

    fun getSelectPosition(): Int {
        return lwvList!!.selectedItem
    }

    fun getSelectStr(): String? {
        return lwvList!!.selectedItemStr
    }
}
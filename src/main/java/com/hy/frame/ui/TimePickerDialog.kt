package com.hy.frame.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.hy.frame.R
import com.hy.frame.app.BaseDialog
import com.hy.frame.widget.loopview.LoopWheelView

/**
 * 选择器
 * @author HeYan
 * @time 2017/7/31 15:07
 */
class TimePickerDialog(context: Context) : BaseDialog(context) {

    private var lwvList1: LoopWheelView? = null
    private var lwvList2: LoopWheelView? = null
    private var btnCancel: Button? = null
    private var btnConfirm: Button? = null
    var datas1: MutableList<String>? = null
    var datas2: MutableList<String>? = null

    override fun getLayoutId(): Int = R.layout.dlg_time_picker

    override fun initWindow() {
        windowDeploy(WindowManager.LayoutParams.MATCH_PARENT.toFloat(), WindowManager.LayoutParams.WRAP_CONTENT.toFloat(), Gravity.BOTTOM)
        window!!.setWindowAnimations(R.style.animBottomInBottomOutStyle)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }

    override fun initView() {
        lwvList1 = findViewById(R.id.picker_lwvList1)
        lwvList2 = findViewById(R.id.picker_lwvList2)
        btnCancel = setOnClickListener(R.id.picker_btnCancel)
        btnConfirm = setOnClickListener(R.id.picker_btnConfirm)
    }

    override fun initData() {
        datas1 = ArrayList()
        (0..23).mapTo(datas1!!) { String.format("%02d", it) }
        datas2 = ArrayList()
        (0..59).mapTo(datas2!!) { String.format("%02d", it) }
        lwvList1?.setItems(datas1)
        lwvList2?.setItems(datas2)
    }

    override fun onViewClick(v: View) {
        dismiss()
        when (v.id) {
            R.id.picker_btnConfirm -> listener?.onDlgConfirm(this)
        }
    }

    fun getSelectHour(): String {
        return lwvList1!!.selectedItemStr
    }

    fun getSelectMinute(): String {
        return lwvList2!!.selectedItemStr
    }

    fun getSelectStr(): String? {
        return getSelectHour() + ":" + getSelectMinute()
    }
}
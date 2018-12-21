package com.hy.frame.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.hy.frame.R
import com.hy.frame.ui.BaseDialog
import com.hy.frame.widget.loopview.LoopWheelView

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

    override fun getLayoutId(): Int = R.layout.dlg_picker

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
            lwvList?.setItemsVisibleCount(visibleNumber)
        }
        if (datas != null && datas!!.size < 9)
            lwvList?.setNotLoop()
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
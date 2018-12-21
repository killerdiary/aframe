package com.hy.frame.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.hy.frame.R
import com.hy.frame.ui.BaseDialog
import com.hy.frame.util.FormatUtil


/**
 * 確認提示
 * @author HeYan
 * @time 2016/6/6 11:46
 */
class ConfirmDialog(context: Context, val title: String, val message: String, val confirm: String? = null, val cancel: String? = null, val hideCancel: Boolean = false) : BaseDialog(context) {

    private var txtTitle: TextView? = null
    private var txtContent: TextView? = null
    private var btnConfirm: TextView? = null
    private var btnCancel: TextView? = null
    private var cancelListener: DialogInterface.OnCancelListener? = null

    override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        this.cancelListener = listener
    }

    override fun getLayoutId(): Int {
        return R.layout.dlg_confirm
    }

    override fun initWindow() {
        windowDeploy(0.8f, WindowManager.LayoutParams.WRAP_CONTENT.toFloat(), Gravity.CENTER)
        setCancelable(true)
        //setCanceledOnTouchOutside(true);
    }

    override fun initView() {
        txtTitle = setOnClickListener<TextView>(R.id.confirm_txtTitle)
        txtContent = findViewById<TextView>(R.id.confirm_txtContent)
        btnConfirm = setOnClickListener<TextView>(R.id.confirm_btnConfirm)
        btnCancel = setOnClickListener<TextView>(R.id.confirm_btnCancel)
    }

    override fun initData() {
        txtTitle!!.text = title
        txtContent!!.text = message
        if (FormatUtil.isNoEmpty(confirm)) btnConfirm!!.text = confirm
        if (FormatUtil.isNoEmpty(cancel)) btnCancel!!.text = cancel
        btnCancel!!.visibility = if (hideCancel) View.GONE else View.VISIBLE
//        if (hideCancel)
//            btnConfirm?.setBackgroundResource(R.drawable.btn_selector)
    }


    override fun onViewClick(v: View) {
        this.dismiss()
        when (v.id) {
            R.id.confirm_btnConfirm -> if (listener != null) {
                listener!!.onDlgConfirm(this)
            }
            R.id.confirm_btnCancel -> if (cancelListener != null) {
                cancelListener!!.onCancel(this)
            }
        }
    }

}

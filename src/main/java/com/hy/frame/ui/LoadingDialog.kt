package com.hy.frame.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import com.hy.frame.R
import com.hy.frame.common.BaseDialog

/**
 * 加载对话框
 * author HeYan
 * time 2015/12/11 18:11
 */
class LoadingDialog constructor(context: Context, private var loadMsg: String? = null) : BaseDialog(context) {

    private var txtLoadMsg: TextView? = null

    override fun initLayoutId(): Int = R.layout.dlg_loading
    
    override fun initWindow() {
        windowDeploy(WindowManager.LayoutParams.WRAP_CONTENT.toFloat(), WindowManager.LayoutParams.WRAP_CONTENT.toFloat(), Gravity.CENTER)
    }

    override fun initView() {
        txtLoadMsg = findViewById(R.id.loading_txtLoadMsg)
    }

    override fun initData() {
        if (loadMsg != null)
            txtLoadMsg!!.text = loadMsg
    }

    override fun onViewClick(v: View) {}

    fun updateMsg(msg: String) {
        if (txtLoadMsg != null) {
            txtLoadMsg!!.text = msg
        } else {
            loadMsg = msg
        }
    }

}

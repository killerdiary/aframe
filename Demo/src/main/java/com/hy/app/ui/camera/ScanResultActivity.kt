package com.hy.app.ui.camera

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.hy.app.R
import com.hy.app.common.BaseActivity

/**
 * 二维码扫描结果
 * @author HeYan
 * @time 2017/9/11 16:16
 */
class ScanResultActivity : BaseActivity() {

    private var txtMsg: TextView? = null
    private var data: String? = null

    override fun getLayoutId(): Int = R.layout.act_camera_scan_result

    override fun initView() {
        txtMsg = setOnClickListener(R.id.camera_scan_result_txtMsg)
    }

    override fun initData() {
        initHeaderBack(R.string.camera_scan_result)
        data = bundle?.getString(ARG_DATA)
        if (data.isNullOrEmpty()) {
            finish()
            return
        }
        txtMsg?.text = data
    }

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {
        if (data!!.startsWith("http://") || data!!.startsWith("https://")) {
            toWeb(data!!)
        }
    }

    private fun toWeb(src: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(src)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    companion object {
        private val ARG_DATA = "arg_data"

        fun newArguments(data: String): Bundle {
            val args = Bundle()
            args.putString(ARG_DATA, data)
            return args
        }
    }
}
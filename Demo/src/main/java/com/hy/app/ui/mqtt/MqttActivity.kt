package com.hy.app.ui.mqtt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.hy.app.R
import com.hy.app.common.AppService
import com.hy.app.common.BaseActivity
import com.hy.frame.util.FormatUtil
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.frame.util.MyToast

/**
 * author HeYan
 * time 2016/3/1 14:00
 */
class MqttActivity : BaseActivity() {
    private var editName: EditText? = null
    private var editTo: EditText? = null
    private var editContent: EditText? = null
    private var btnRegist: Button? = null
    private var btnSend: Button? = null
    private var txtMsg: TextView? = null

    override fun getLayoutId(): Int {
        return R.layout.act_mqtt
    }

    override fun initView() {
        editName = findViewById(R.id.editName)
        editTo = findViewById(R.id.editTo)
        editContent = findViewById(R.id.editContent)
        btnRegist = setOnClickListener(R.id.btnRegist)
        btnSend = setOnClickListener(R.id.btnSend)
        txtMsg = findViewById(R.id.txtMsg)
    }

    override fun initData() {}

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {
        when (v.id) {
            R.id.btnRegist -> regist()
            R.id.btnSend -> sendMsg()
        }
    }

    private fun sendMsg() {
        val name = editTo!!.text.toString()
        if (FormatUtil.isEmpty(name)) {
            MyToast.show(getCurContext()!!, "对方用户名不能为空")
            return
        }
        val content = editContent!!.text.toString()
        if (FormatUtil.isEmpty(content)) {
            MyToast.show(getCurContext()!!, "内容不能为空")
            return
        }
        //        Intent intent = new Intent(AppService.ACTION_PUSH_SEND);
        //        intent.putExtra(Constant.FLAG, name);
        //        intent.putExtra(Constant.FLAG2, content);
        //        sendBroadcast(intent);
    }

    private fun regist() {
        val name = editName!!.text.toString()
        if (FormatUtil.isEmpty(name)) {
            MyToast.show(getCurContext()!!, "当前用户名不能为空")
            return
        }
        //        Intent intent = new Intent(getCurContext(), AppService.class);
        //        intent.putExtra(Constant.FLAG, name);
        //        startService(intent);
    }

    private var filter: IntentFilter? = null
    private var receiver: BroadcastReceiver? = null

    fun registerReceiver() {
        if (filter == null) {
            filter = IntentFilter(AppService.ACTION_PUSH)
            filter!!.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        }
        if (receiver == null) {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    MyLog.d("ChatActivity: 接收到消息")
                    val action = intent.action
                    if (TextUtils.equals(action, AppService.ACTION_PUSH)) {
                        //                        String content = intent.getStringExtra(Constant.FLAG);
                        //                        if (content != null) {
                        //                            txtMsg.append(content);
                        //                            txtMsg.append("\n");
                        //                        }
                        this.abortBroadcast()
                    }
                }
            }
        }
        registerReceiver(receiver, filter)
    }


    override fun onStart() {
        super.onStart()
        registerReceiver()
    }

    override fun onStop() {
        super.onStop()
        if (receiver != null)
            unregisterReceiver(receiver)
    }

}

package com.hy.app.ui

import android.view.View
import android.widget.TextView

import com.hy.app.R
import com.hy.app.common.BaseActivity

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader


/**
 * @author HeYan
 * @title
 * @time 2015/10/21 17:11
 */
class CmdActivity : BaseActivity() {
    private var txtMsg: TextView? = null

    override fun getLayoutId(): Int {
        return R.layout.act_cmd
    }

    override fun initView() {
        initHeaderBack(R.string.cmd, 0)
        setOnClickListener<View>(R.id.button0)
        setOnClickListener<View>(R.id.button1)
        setOnClickListener<View>(R.id.button2)
        txtMsg = findViewById(R.id.textView)
    }

    override fun initData() {}

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {
        when (v.id) {
            R.id.button0 -> do_exec("su")
            R.id.button1 -> exe("setprop service.adb.tcp.port 5555", "stop adbd", "start adbd")
            R.id.button2 -> do_exec("netcfg")
        }
    }

    fun exe(cmd1: String, cmd2: String, cmd3: String) {
        try {
            val proc = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(proc.outputStream)
            os.writeBytes(cmd1 + "\n")
            os.writeBytes(cmd2 + "\n")
            os.writeBytes(cmd3 + "\n")
            os.writeBytes("exit\n")
            os.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun do_exec(cmd: String): String {
        val sb = StringBuilder()
        sb.append("\n")
        try {
            val p = Runtime.getRuntime().exec(cmd)
            val reader = BufferedReader(InputStreamReader(p.inputStream))
            var line: String? = reader.readLine()
            while (line != null) {
                sb.append(line + "\n")
                line = reader.readLine()
            }
            val os = DataOutputStream(p.outputStream)
            os.writeBytes("exit\n")
            os.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        txtMsg!!.text = sb.toString()
        return cmd
    }
}

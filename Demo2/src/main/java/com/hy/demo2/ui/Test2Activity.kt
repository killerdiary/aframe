package com.hy.demo2.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.hy.demo2.R
import com.hy.frame.util.MyLog

/**
 * title 无
 * author heyan
 * time 18-12-18 下午3:44
 * desc 无
 */
class Test2Activity : com.hy.frame.ui.AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        MyLog.d(javaClass, "test")
        Toast.makeText(this@Test2Activity, "button1", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,Test1Activity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
    }

    fun getLayoutId(): Int = R.layout.v_test2

    fun initView() {
        //getTemplateControl()?.setHeaderLeft(R.drawable.v_back)

        setTitle(R.string.appName)
        val btn = findViewById<View>(R.id.button1)

        btn.setOnClickListener(this)
        MyLog.d(javaClass, "test initView" + btn)
        MyLog.d(javaClass, "test initView" + R.id.button)
        val btn1 = findViewById<TextView>(R.id.txtTest1)
        btn1.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@Test2Activity, "txtTest1", Toast.LENGTH_SHORT).show()
                MyLog.d(javaClass, "test " + v?.id)
                btn1?.text = "test"
            }
        })
    }

    fun initData() {
    }


}
package com.hy.demo2.ui

import android.content.pm.ActivityInfo
import android.view.View
import android.widget.TextView
import com.hy.demo2.R
import com.hy.demo2.app.BaseActivity

import com.hy.frame.util.MyLog

/**
 * title 无
 * author heyan
 * time 18-12-18 下午3:44
 * desc 无
 */
class Test1Activity : BaseActivity() {
    override fun isPermissionDenied(): Boolean = false
    override fun isSingleLayout(): Boolean = false
    override fun isTranslucentStatus(): Boolean = false
    override fun getScreenOrientation(): Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    override fun getLayoutId(): Int = R.layout.v_test1

    override fun initView() {
        getTemplateControl()?.setHeaderLeft(R.drawable.v_back)

        setTitle(R.string.appName)
        val btn = findViewById<View>(R.id.button)
        btn.isClickable = true
        btn.setOnClickListener(this)
        MyLog.d(javaClass,"test initView" + btn)
        MyLog.d(javaClass,"test initView" + R.id.button)
        val btn1 = findViewById<TextView>(R.id.button1)
        btn1.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                MyLog.d(javaClass,"test " + v?.id)
                btn1?.text = "test"
            }
        })
    }

    override fun initData() {
    }

    override fun onClick(v: View) {
        MyLog.d(javaClass,"test")
        super.onClick(v)
    }
    override fun onViewClick(v: View) {

    }
}
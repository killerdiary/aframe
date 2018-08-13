package com.hy.app.ui

import android.view.View
import android.widget.TextView
import com.hy.app.BuildConfig
import com.hy.app.R
import com.hy.app.common.BaseActivity
import com.hy.frame.util.RxTimerUtil

class LaunchActivity : BaseActivity(), RxTimerUtil.ICallback {


    private var txtVersion: TextView? = null
    private var timerUtil: RxTimerUtil? = null
    override fun isSingleLayout(): Boolean = true
    override fun getLayoutId(): Int = R.layout.act_launch
    override fun initView() {
        txtVersion = findViewById(R.id.launch_txtVersion)
    }

    override fun initData() {
        txtVersion?.text = "Version" + BuildConfig.VERSION_NAME
        timerUtil = RxTimerUtil()
        timerUtil?.timer(2500L, this)
    }

    override fun onViewClick(v: View) {

    }

    override fun doNext(milliseconds: Long) {
        startAct(MainActivity::class.java)
        finish()
        overridePendingTransition(0, 0)

    }

    override fun onDestroy() {
        super.onDestroy()
        timerUtil?.cancel()
    }
}
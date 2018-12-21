package com.hy.demo1.ui

import android.view.View
import com.hy.demo1.R

class MainActivity : BaseActivity() {


    override fun getLayoutId(): Int  = R.layout.act_main

    override fun initView() {

    }

    override fun initData() {
        setTitle(R.string.app_name)
    }

    override fun onViewClick(v: View) {

    }
}

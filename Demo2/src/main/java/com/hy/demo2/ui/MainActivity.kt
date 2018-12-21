package com.hy.demo2.ui

import android.view.View
import com.hy.demo2.R

class MainActivity : MenuActivity() {

    override fun initData() {
        intent.putExtra(BUNDLE, MenuActivity.newArguments(R.xml.menu_main, R.string.app_name))
        super.initData()
        getTemplateControl()?.getHeaderLeft()?.visibility = View.GONE
    }
}

package com.hy.app.ui

import android.view.View
import com.hy.app.R

/**
 * 主页
 * @author HeYan
 * @time 2017/9/11 16:56
 */
class MainActivity : MenuActivity() {

    override fun initData() {
        intent.putExtra(BUNDLE, MenuActivity.newArguments(R.xml.menu_main, R.string.app_name))
        super.initData()
        headerLeft.visibility = View.GONE
        //startAct(RecyclerActivity::class.java)
    }

}

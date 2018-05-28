package com.hy.app.ui

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup

import com.hy.app.R
import com.hy.app.common.BaseActivity

/**
 * @author HeYan
 * @title TestActvity
 * @time 2016/5/24 10:50
 */
class TestActvity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.act_test
    }

    override fun initView() {

    }

    override fun initData() {
        initHeaderBack(R.string.radiogroup_test, 0)


    }

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {}
}

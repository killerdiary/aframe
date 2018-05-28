package com.hy.app.ui.load

import android.view.View

import com.hy.app.R
import com.hy.app.common.BaseActivity
import com.hy.app.widget.load.SuperLoadingProgress


class LoadActivity : BaseActivity() {
    var mSuperLoadingProgress: SuperLoadingProgress? = null

    override fun getLayoutId(): Int {
        return R.layout.act_load
    }

    override fun initView() {
        initHeaderBack(R.string.load, 0)
        mSuperLoadingProgress = findViewById<View>(R.id.pro) as SuperLoadingProgress
    }

    override fun initData() {
        findViewById<View>(R.id.btn).setOnClickListener {
            object : Thread() {
                override fun run() {
                    try {
                        mSuperLoadingProgress?.progress = 0
                        while (mSuperLoadingProgress?.progress?: 0 < 100) {
                            Thread.sleep(10)
                            mSuperLoadingProgress?.progress = mSuperLoadingProgress?.progress ?:0 + 1
                        }
                        mSuperLoadingProgress?.finishFail()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }.start()
        }

        findViewById<View>(R.id.btn2).setOnClickListener {
            object : Thread() {
                override fun run() {
                    try {
                        var circle = 0
                        mSuperLoadingProgress?.progress = 0
                        while (circle < 10) {
                            Thread.sleep(10)
                            if (mSuperLoadingProgress?.progress == 100) {
                                mSuperLoadingProgress?.progress = 0
                                circle++
                            } else
                                mSuperLoadingProgress?.progress = mSuperLoadingProgress?.progress ?: 0 + 1
                        }
                        mSuperLoadingProgress?.finishSuccess()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }.start()
        }
    }

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {}
}

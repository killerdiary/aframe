package com.hy.app.common

import com.tencent.smtt.sdk.QbSdk


/**
 * @author HeYan
 * @title
 * @time 2015/11/20 18:08
 */
class MyApplication : com.hy.frame.app.BaseApplication() {

    override fun isLoggable(): Boolean = true

    override fun onCreate() {
        super.onCreate()
        app = this

        //x5内核初始化接口
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {

            }

            override fun onViewInitFinished(p0: Boolean) {

            }
        })
        QbSdk.setDownloadWithoutWifi(false)

    }


    companion object {
        var app: MyApplication? = null
            private set
    }
}

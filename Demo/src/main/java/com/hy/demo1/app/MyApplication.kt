package com.hy.demo1.app

import com.hy.demo1.BuildConfig
import com.hy.frame.app.BaseApplication

class MyApplication :BaseApplication() {

    override fun isLoggable(): Boolean = BuildConfig.DEBUG

    override fun isMultiDex(): Boolean  = false
}
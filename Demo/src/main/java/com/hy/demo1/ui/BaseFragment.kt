package com.hy.demo1.ui

abstract class BaseFragment : com.hy.frame.ui.BaseFragment() {

    override fun isSingleLayout(): Boolean = false

    override fun onRestart() {

    }

    override fun sendMsg(flag: Int, obj: Any?) {

    }
}
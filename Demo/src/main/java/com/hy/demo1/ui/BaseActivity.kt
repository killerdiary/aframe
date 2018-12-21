package com.hy.demo1.ui

abstract class BaseActivity : com.hy.frame.ui.BaseActivity() {
    override fun isPermissionDenied(): Boolean = false
    override fun isSingleLayout(): Boolean = false
    override fun isTranslucentStatus(): Boolean = false
}
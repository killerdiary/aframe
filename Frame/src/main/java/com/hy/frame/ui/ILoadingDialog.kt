package com.hy.frame.ui

/**
 * title 无
 * author heyan
 * time 19-3-4 下午3:11
 * desc 无
 */
interface ILoadingDialog {
    fun updateMessage(msg: String?)
    fun cancel()
    fun dismiss()
    fun show()
}
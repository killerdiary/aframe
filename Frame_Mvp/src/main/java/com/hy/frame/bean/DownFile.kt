package com.hy.frame.bean

import java.io.File

/**
 * com.hy.frame.bean
 * author HeYan
 * time 2016/9/2 15:44
 */
class DownFile {
    var state: Int = 0 //下载状态
    var fileSize: Long = 0 //文件总大小
    var filePath: String? = null //文件路径
    var downloadSize: Long = 0 //当前文件大小
    var progress: Float = 0F //进度
        get() {
            if (fileSize > 0 && downloadSize > 0) {
                field = downloadSize.toFloat() / fileSize
            }
            return field
        }
    var allCount: Long = 0

    var fileName: String? = null
    var url: String? = null
    var isRange: Boolean = false
    var isDeleteOld: Boolean = false


    companion object {

        val STATUS_START = 0
        val STATUS_PROGRESS = 1
        val STATUS_SUCCESS = 2
        val STATUS_ERROR = -1
    }
}

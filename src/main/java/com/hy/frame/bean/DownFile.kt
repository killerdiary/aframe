package com.hy.frame.bean

import java.io.File

/**
 * com.hy.frame.bean
 * author HeYan
 * time 2016/9/2 15:44
 */
class DownFile {
    var state: Int = 0
    var progress: Int = 0
    var fileCount: Long = 0
    var allCount: Long = 0
    var saveDir: String? = null
    var fileName: String? = null
    var url: String? = null
    var isRange: Boolean = false
    var isDeleteOld: Boolean = false

    val filePath: String
        get() = saveDir + File.separator + fileName

    companion object {

        val STATUS_START = 0
        val STATUS_PROGRESS = 1
        val STATUS_SUCCESS = 2
        val STATUS_ERROR = -1
    }
}

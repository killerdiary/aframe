package com.hy.http.file

import com.hy.frame.util.FileUtil
import java.io.File
import java.io.IOException

class FileBinary(override val file: File, fileName: String? = file.name, mimeType: String? = null) : Binary {

    override val fileName: String
    override val mimeType: String
    override val length: Long

    companion object {
        val MIME_TYPE_FILE = "application/octet-stream"
    }

    init {
        if (!file.exists()) {
            throw IOException("File isn't exists")
        }
        this.length = file.length()
        this.fileName = fileName!!
        this.mimeType = if (mimeType.isNullOrEmpty()) FileUtil.getMimeTypeByUrl(file.absolutePath) else MIME_TYPE_FILE
    }
}

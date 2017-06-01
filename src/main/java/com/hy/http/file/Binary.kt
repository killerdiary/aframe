package com.hy.http.file

import java.io.File

interface Binary {

    val file: File

    /**
     * Length of byteArray.
     * @return Long length.
     */
    val length: Long

    /**
     * Return the fileName, Can be null.
     * @return File name.
     */
    val fileName: String

    /**
     * Return mimeType of binary.
     * @return MimeType.
     */
    val mimeType: String
}
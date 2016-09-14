package com.hy.http.file;

import java.io.File;

public interface Binary {

    File getFile();

    /**
     * Length of byteArray.
     *
     * @return Long length.
     */
    long getLength();

    /**
     * Return the fileName, Can be null.
     *
     * @return File name.
     */
    String getFileName();

    /**
     * Return mimeType of binary.
     *
     * @return MimeType.
     */
    String getMimeType();
}

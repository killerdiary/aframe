package com.hy.http.file;

import android.text.TextUtils;

import com.hy.frame.util.FileUtil;
import com.hy.frame.util.MyLog;

import java.io.File;

public class FileBinary implements Binary {

    private File file;
    private String fileName;
    private String mimeType;
    public static final String MIME_TYPE_FILE = "application/octet-stream";

    public FileBinary(File file) {
        this(file, file.getName());
    }

    public FileBinary(File file, String fileName) {
        this(file, fileName, null);
    }

    public FileBinary(File file, String fileName, String mimeType) {
        if (file == null) {
            MyLog.w("File == null");
        } else if (!file.exists()) {
            MyLog.w("File isn't exists");
        }
        this.file = file;
        this.fileName = fileName;
        this.mimeType = mimeType;
    }


    @Override
    public File getFile() {
        return file;
    }

    @Override
    public long getLength() {
        if (file == null || !file.exists())
            return 0;
        return this.file.length();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getMimeType() {
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = FileUtil.getMimeTypeByUrl(file.getAbsolutePath());
            if (TextUtils.isEmpty(mimeType))
                mimeType = MIME_TYPE_FILE;
        }
        return mimeType;
    }
}

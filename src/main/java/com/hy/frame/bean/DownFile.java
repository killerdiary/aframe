package com.hy.frame.bean;

import java.io.File;

/**
 * com.hy.frame.bean
 * author HeYan
 * time 2016/9/2 15:44
 */
public class DownFile {

    public static final int STATUS_START = 0;
    public static final int STATUS_PROGRESS = 1;
    public static final int STATUS_SUCCESS = 2;
    public static final int STATUS_ERROR = -1;
    private int state;
    private int progress;
    private long fileCount;
    private long allCount;
    private String saveDir;
    private String fileName;
    private String url;
    private boolean isRange;
    private boolean isDeleteOld;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
    }

    public long getAllCount() {
        return allCount;
    }

    public void setAllCount(long allCount) {
        this.allCount = allCount;
    }

    public String getSaveDir() {
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        this.saveDir = saveDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRange() {
        return isRange;
    }

    public void setRange(boolean range) {
        isRange = range;
    }

    public boolean isDeleteOld() {
        return isDeleteOld;
    }

    public void setDeleteOld(boolean deleteOld) {
        isDeleteOld = deleteOld;
    }

    public String getFilePath() {
        return saveDir + File.separator + fileName;
    }
}

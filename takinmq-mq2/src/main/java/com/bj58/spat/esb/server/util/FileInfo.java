package com.bj58.spat.esb.server.util;

import java.io.File;
import java.io.IOException;

public class FileInfo {

    private String fileName;

    private String filePath;

    private long fileSize;

    private long lastModifyTime;

    public FileInfo(File f) throws IOException {
        this.fileName = f.getName();
        this.filePath = f.getCanonicalPath();
        this.fileSize = f.length();
        this.lastModifyTime = f.lastModified();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}

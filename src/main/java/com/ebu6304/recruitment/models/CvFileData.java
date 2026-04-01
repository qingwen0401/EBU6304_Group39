package com.ebu6304.recruitment.models;

/**
 * CV 文件读取结果，供控制层返回给前端接口使用。
 */
public class CvFileData {
    private final String fileName;
    private final String contentType;
    private final byte[] content;

    public CvFileData(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}

package com.data4truth.mos.service;

/**
 * @author wushan
 * @version : com.data4truth.pi.Model.Dto, v 0.1 2019/12/9 0009 上午 10:41 wushan Exp $$
 * @desc : 文件实体Dto
 */
public class FileDto {

    private String fileName;

    private String fileSuffix;

    private String fileId;

    private int    fileSize;

    /**
     * Getter method for property <tt>fileSize</tt>.
     *
     * @return property value of fileSize
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * Setter method for property <tt>fileSize</tt>.
     *
     * @param fileSize value to be assigned to property fileSize
     */
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Getter method for property <tt>fileName</tt>.
     *
     * @return property value of fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Setter method for property <tt>fileName</tt>.
     *
     * @param fileName value to be assigned to property fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Getter method for property <tt>fileSuffix</tt>.
     *
     * @return property value of fileSuffix
     */
    public String getFileSuffix() {
        return fileSuffix;
    }

    /**
     * Setter method for property <tt>fileSuffix</tt>.
     *
     * @param fileSuffix value to be assigned to property fileSuffix
     */
    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    /**
     * Getter method for property <tt>fileId</tt>.
     *
     * @return property value of fileId
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * Setter method for property <tt>fileId</tt>.
     *
     * @param fileId value to be assigned to property fileId
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public static void getPreviewUrl(FileDto fileDto) {

    }
}



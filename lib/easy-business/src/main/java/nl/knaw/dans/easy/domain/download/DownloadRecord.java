package nl.knaw.dans.easy.domain.download;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;

import org.joda.time.DateTime;

public class DownloadRecord extends AbstractJiBXObject<DownloadRecord> {

    private static final long serialVersionUID = -1530198797467785477L;

    private String datasetId;
    private String fileItemId;
    private String path;
    private String mimeType;
    private long size;

    private String downloaderId;
    private DateTime downloadTime;

    public DownloadRecord() {

    }

    public DownloadRecord(String fileItemId) {
        this.fileItemId = fileItemId;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public String getFileItemId() {
        return fileItemId;
    }

    public void setFileItemId(String fileItemId) {
        this.fileItemId = fileItemId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDownloaderId() {
        return downloaderId;
    }

    public void setDownloaderId(String downloaderId) {
        this.downloaderId = downloaderId;
    }

    public DateTime getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(DateTime downloadTime) {
        this.downloadTime = downloadTime;
    }

}

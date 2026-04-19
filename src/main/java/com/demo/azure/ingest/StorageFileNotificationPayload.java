package com.demo.azure.ingest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** JSON 正文字段约定；由 {@link com.demo.azure.storage.LocalStorageFileNotificationFactory} 构造并与本类对齐。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorageFileNotificationPayload {

    private String traceId;
    private String source;
    private String path;
    private String fileName;
    private String detectedAt;
    private Long sizeBytes;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(String detectedAt) {
        this.detectedAt = detectedAt;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }
}

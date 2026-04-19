package com.demo.azure.storage.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Virtual order XML write result")
public class VirtualOrderResponse {

    private String orderNo;
    private String absolutePath;
    private String relativeToStorageRoot;
    private String fileName;

    public VirtualOrderResponse() {}

    public VirtualOrderResponse(String orderNo, String absolutePath, String relativeToStorageRoot, String fileName) {
        this.orderNo = orderNo;
        this.absolutePath = absolutePath;
        this.relativeToStorageRoot = relativeToStorageRoot;
        this.fileName = fileName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getRelativeToStorageRoot() {
        return relativeToStorageRoot;
    }

    public void setRelativeToStorageRoot(String relativeToStorageRoot) {
        this.relativeToStorageRoot = relativeToStorageRoot;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

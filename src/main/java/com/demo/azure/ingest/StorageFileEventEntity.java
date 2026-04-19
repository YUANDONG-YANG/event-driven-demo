package com.demo.azure.ingest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "storage_file_events")
public class StorageFileEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128)
    private String source;

    @Column(name = "file_path", length = 4096)
    private String filePath;

    @Column(name = "file_name", length = 1024)
    private String fileName;

    @Column(name = "detected_at")
    private Instant detectedAt;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(length = 512)
    private String subject;

    @Column(name = "service_bus_message_id", length = 256)
    private String serviceBusMessageId;

    /** Same id as log MDC / message correlationId for end-to-end tracing. */
    @Column(name = "trace_id", length = 64)
    private String traceId;

    @Column(name = "raw_payload", columnDefinition = "text")
    private String rawPayload;

    @CreationTimestamp
    @Column(name = "persisted_at", nullable = false, updatable = false)
    private Instant persistedAt;

    public Long getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(Instant detectedAt) {
        this.detectedAt = detectedAt;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getServiceBusMessageId() {
        return serviceBusMessageId;
    }

    public void setServiceBusMessageId(String serviceBusMessageId) {
        this.serviceBusMessageId = serviceBusMessageId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public Instant getPersistedAt() {
        return persistedAt;
    }
}

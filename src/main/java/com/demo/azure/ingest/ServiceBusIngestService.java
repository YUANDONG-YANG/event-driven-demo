package com.demo.azure.ingest;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.demo.azure.trace.PipelineTrace;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
public class ServiceBusIngestService {

    private static final Logger log = LoggerFactory.getLogger(ServiceBusIngestService.class);

    private final StorageFileEventRepository repository;
    private final ObjectMapper objectMapper;
    private final ServiceBusTraceIdResolver traceIdResolver;

    public ServiceBusIngestService(
            StorageFileEventRepository repository,
            ObjectMapper objectMapper,
            ServiceBusTraceIdResolver traceIdResolver) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.traceIdResolver = traceIdResolver;
    }

    @Transactional
    public void persistFromServiceBus(ServiceBusReceivedMessage message, String bodyUtf8) {
        String traceId = traceIdResolver.resolve(message, bodyUtf8);
        PipelineTrace.put(traceId);
        try {
            log.info(
                    "[pipeline=5_DB_PERSIST] begin messageId={} correlationId={} traceId={}",
                    message.getMessageId(),
                    message.getCorrelationId(),
                    traceId);

            StorageFileEventEntity row = new StorageFileEventEntity();
            row.setTraceId(traceId);
            row.setServiceBusMessageId(message.getMessageId());
            row.setSubject(message.getSubject());
            row.setRawPayload(bodyUtf8);

            try {
                StorageFileNotificationPayload p = objectMapper.readValue(bodyUtf8, StorageFileNotificationPayload.class);
                if (p.getTraceId() != null && !p.getTraceId().isBlank()) {
                    row.setTraceId(p.getTraceId());
                }
                row.setSource(p.getSource());
                row.setFilePath(p.getPath());
                row.setFileName(p.getFileName());
                row.setSizeBytes(p.getSizeBytes());
                if (p.getDetectedAt() != null && !p.getDetectedAt().isBlank()) {
                    try {
                        row.setDetectedAt(Instant.parse(p.getDetectedAt()));
                    } catch (DateTimeParseException ex) {
                        log.debug("Could not parse detectedAt as Instant: {}", p.getDetectedAt());
                    }
                }
            } catch (Exception parseEx) {
                log.warn(
                        "[pipeline=5_DB_PERSIST] body is not expected JSON; storing raw only traceId={} reason={}",
                        traceId,
                        parseEx.getMessage());
            }

            StorageFileEventEntity saved = repository.save(row);
            log.info(
                    "[pipeline=5_DB_PERSIST] ok entityId={} traceId={} fileName={} sbMessageId={}",
                    saved.getId(),
                    saved.getTraceId(),
                    saved.getFileName(),
                    message.getMessageId());
        } finally {
            PipelineTrace.clear();
        }
    }
}

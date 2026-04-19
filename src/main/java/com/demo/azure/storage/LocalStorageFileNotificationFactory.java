package com.demo.azure.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps {@link StorageFileDetectedEvent} to the same field shape as {@link com.demo.azure.ingest.StorageFileNotificationPayload}
 * for Event Grid (CloudEvent data) and Service Bus (JSON body), avoiding duplicate hand-built maps.
 */
@Component
public class LocalStorageFileNotificationFactory {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageFileNotificationFactory.class);

    public Map<String, Object> forEventGrid(StorageFileDetectedEvent event) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("traceId", event.traceId());
        data.put("path", event.absolutePath().toString());
        data.put("fileName", event.absolutePath().getFileName().toString());
        data.put("detectedAt", event.detectedAt().toString());
        return data;
    }

    public Map<String, Object> forServiceBus(StorageFileDetectedEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("traceId", event.traceId());
        payload.put("source", "local-storage");
        payload.put("path", event.absolutePath().toString());
        payload.put("fileName", event.absolutePath().getFileName().toString());
        payload.put("detectedAt", event.detectedAt().toString());
        try {
            payload.put("sizeBytes", Files.size(event.absolutePath()));
        } catch (Exception ex) {
            log.debug("Could not read file size for {}: {}", event.absolutePath(), ex.toString());
        }
        return payload;
    }
}

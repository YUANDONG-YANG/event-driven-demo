package com.demo.azure.storage;

import java.nio.file.Path;
import java.time.Instant;

/**
 * Fired when a new file is created under the watched storage tree. {@code traceId} correlates logs across
 * storage → Event Grid → Service Bus → DB.
 */
public record StorageFileDetectedEvent(Path absolutePath, Instant detectedAt, String traceId) {}

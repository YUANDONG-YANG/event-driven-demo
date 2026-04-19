package com.demo.azure.ingest;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Resolves pipeline {@code traceId} from Service Bus correlation id and JSON body; shared by the consumer entry point
 * and persistence logic to avoid duplication.
 */
@Component
public class ServiceBusTraceIdResolver {

    private final ObjectMapper objectMapper;

    public ServiceBusTraceIdResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String resolve(ServiceBusReceivedMessage message, String bodyUtf8) {
        if (message.getCorrelationId() != null && !message.getCorrelationId().isBlank()) {
            return message.getCorrelationId();
        }
        try {
            if (bodyUtf8 != null && bodyUtf8.contains("\"traceId\"")) {
                String t = objectMapper.readTree(bodyUtf8).path("traceId").asText("");
                if (!t.isBlank()) {
                    return t;
                }
            }
        } catch (Exception ignored) {
            // fall through
        }
        return "no-trace";
    }
}

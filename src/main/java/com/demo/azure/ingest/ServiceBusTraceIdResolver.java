package com.demo.azure.ingest;

import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * 从 Service Bus 消息（关联 ID 与 JSON 正文）解析链路 traceId；供消费入口与落库逻辑共用，避免重复实现。
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

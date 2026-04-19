package com.demo.azure.servicebus;

import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.demo.azure.ingest.ServiceBusIngestService;
import com.demo.azure.ingest.ServiceBusTraceIdResolver;
import com.demo.azure.trace.PipelineTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Service Bus 处理器回调：日志、trace、委托落库、完成/放弃消息。与 {@link ServiceBusProcessorConfig} 的 Bean 装配职责分离（单一职责）。
 */
@Component
public class ServiceBusIngestMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceBusIngestMessageHandler.class);

    private final ServiceBusIngestService ingestService;
    private final ServiceBusTraceIdResolver traceIdResolver;

    public ServiceBusIngestMessageHandler(
            ServiceBusIngestService ingestService, ServiceBusTraceIdResolver traceIdResolver) {
        this.ingestService = ingestService;
        this.traceIdResolver = traceIdResolver;
    }

    public void onMessage(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage message = context.getMessage();
        String body = message.getBody() != null ? message.getBody().toString() : "";
        String traceId = traceIdResolver.resolve(message, body);
        PipelineTrace.put(traceId);
        try {
            log.info(
                    "[pipeline=4_SERVICE_BUS_RECEIVE] messageId={} correlationId={} subject={} deliveryCount={} traceId={} bodyChars={}",
                    message.getMessageId(),
                    message.getCorrelationId(),
                    message.getSubject(),
                    message.getDeliveryCount(),
                    traceId,
                    body.length());
            if (log.isDebugEnabled()) {
                log.debug(
                        "[pipeline=4_SERVICE_BUS_RECEIVE] bodyPreview={}",
                        body.length() > 500 ? body.substring(0, 500) + "..." : body);
            }
            ingestService.persistFromServiceBus(message, body);
            context.complete();
            log.info(
                    "[pipeline=4_SERVICE_BUS_RECEIVE] completed ok messageId={} traceId={}",
                    message.getMessageId(),
                    traceId);
        } catch (Exception e) {
            log.error(
                    "[pipeline=4_SERVICE_BUS_RECEIVE] failed messageId={} traceId={}",
                    message.getMessageId(),
                    traceId,
                    e);
            context.abandon();
        } finally {
            PipelineTrace.clear();
        }
    }

    public void onError(ServiceBusErrorContext context) {
        log.warn("Service Bus processing error: {}", context.getException().getMessage());
    }
}

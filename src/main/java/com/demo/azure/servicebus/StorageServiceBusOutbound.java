package com.demo.azure.servicebus;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.demo.azure.storage.LocalStorageFileNotificationFactory;
import com.demo.azure.storage.StorageFileDetectedEvent;
import com.demo.azure.trace.PipelineTrace;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * Sends a JSON message to Service Bus when {@link StorageFileDetectedEvent} fires (local storage → Service Bus).
 */
@Component
@ConditionalOnBean(name = "storageServiceBusSender")
@ConditionalOnProperty(prefix = "demo.storage", name = "forward-to-service-bus", havingValue = "true")
public class StorageServiceBusOutbound {

    private static final Logger log = LoggerFactory.getLogger(StorageServiceBusOutbound.class);

    private final ServiceBusSenderClient sender;
    private final ObjectMapper objectMapper;
    private final LocalStorageFileNotificationFactory notificationFactory;

    public StorageServiceBusOutbound(
            @Qualifier("storageServiceBusSender") ServiceBusSenderClient sender,
            ObjectMapper objectMapper,
            LocalStorageFileNotificationFactory notificationFactory) {
        this.sender = sender;
        this.objectMapper = objectMapper;
        this.notificationFactory = notificationFactory;
    }

    @EventListener
    public void onStorageFile(StorageFileDetectedEvent event) {
        PipelineTrace.put(event.traceId());
        try {
            log.info(
                    "[pipeline=3_SERVICE_BUS_SEND] begin path={} traceId={}",
                    event.absolutePath(),
                    event.traceId());
            String json = objectMapper.writeValueAsString(notificationFactory.forServiceBus(event));
            ServiceBusMessage message = new ServiceBusMessage(json);
            message.setContentType("application/json");
            message.setSubject("LocalStorage.FileCreated");
            message.setCorrelationId(event.traceId());
            sender.sendMessage(message);
            log.info(
                    "[pipeline=3_SERVICE_BUS_SEND] ok traceId={} subject=LocalStorage.FileCreated path={}",
                    event.traceId(),
                    event.absolutePath());
        } catch (Exception e) {
            log.error(
                    "[pipeline=3_SERVICE_BUS_SEND] failed traceId={} path={}",
                    event.traceId(),
                    event.absolutePath(),
                    e);
        } finally {
            PipelineTrace.clear();
        }
    }
}

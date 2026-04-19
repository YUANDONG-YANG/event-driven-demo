package com.demo.azure.storage;

import com.azure.core.models.CloudEvent;
import com.azure.core.models.CloudEventDataFormat;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.demo.azure.trace.PipelineTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


/**
 * Forwards local storage file events to a custom Event Grid topic. In Azure, the usual pattern is Blob Storage +
 * Event Grid; this bridges local watch → CloudEvent.
 */
@Component
@ConditionalOnBean(EventGridPublisherClient.class)
@ConditionalOnProperty(prefix = "demo.storage", name = "forward-to-event-grid", havingValue = "true")
public class StorageEventGridBridge {

    private static final Logger log = LoggerFactory.getLogger(StorageEventGridBridge.class);

    private final EventGridPublisherClient<CloudEvent> publisher;
    private final LocalStorageFileNotificationFactory notificationFactory;

    public StorageEventGridBridge(
            EventGridPublisherClient<CloudEvent> publisher, LocalStorageFileNotificationFactory notificationFactory) {
        this.publisher = publisher;
        this.notificationFactory = notificationFactory;
    }

    @EventListener
    public void onStorageFile(StorageFileDetectedEvent event) {
        PipelineTrace.put(event.traceId());
        try {
            log.info(
                    "[pipeline=2_EVENT_GRID_PUBLISH] begin type=LocalStorage.FileCreated path={}",
                    event.absolutePath());
            CloudEvent cloudEvent = new CloudEvent(
                    "/demo/local-storage",
                    "LocalStorage.FileCreated",
                    BinaryData.fromObject(notificationFactory.forEventGrid(event)),
                    CloudEventDataFormat.JSON,
                    "application/json");
            publisher.sendEvent(cloudEvent);
            log.info(
                    "[pipeline=2_EVENT_GRID_PUBLISH] ok traceId={} subject=LocalStorage.FileCreated",
                    event.traceId());
        } catch (RuntimeException e) {
            log.error(
                    "[pipeline=2_EVENT_GRID_PUBLISH] failed traceId={} path={}",
                    event.traceId(),
                    event.absolutePath(),
                    e);
        } finally {
            PipelineTrace.clear();
        }
    }
}

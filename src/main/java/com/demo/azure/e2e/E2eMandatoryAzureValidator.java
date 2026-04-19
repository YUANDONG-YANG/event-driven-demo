package com.demo.azure.e2e;

import com.demo.azure.config.AzureDemoProperties;
import com.demo.azure.storage.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Validates Azure settings for pipeline simulation profiles:
 * <ul>
 *   <li>{@code e2e} — shortcut: publish to Event Grid <strong>and</strong> send directly to Service Bus (local dev).
 *   <li>{@code e2e-azure} — closer to cloud: only publish to Event Grid; Service Bus receives messages only via Portal
 *   Event Subscription (Event Grid → Service Bus), then this app consumes → DB.
 * </ul>
 */
@Component
@Profile({"e2e", "e2e-azure"})
@Order(0)
public class E2eMandatoryAzureValidator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(E2eMandatoryAzureValidator.class);

    private final AzureDemoProperties props;
    private final StorageProperties storageProperties;

    public E2eMandatoryAzureValidator(AzureDemoProperties props, StorageProperties storageProperties) {
        this.props = props;
        this.storageProperties = storageProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        var eg = props.getEventGrid();
        var sb = props.getServiceBus();

        if (eg.getTopicEndpoint() == null || eg.getTopicEndpoint().isBlank()) {
            throw new IllegalStateException(
                    "E2E profile requires Azure Event Grid: set demo.azure.event-grid.topic-endpoint or env AZURE_EVENTGRID_TOPIC_ENDPOINT.");
        }
        if (eg.getTopicKey() == null || eg.getTopicKey().isBlank()) {
            throw new IllegalStateException(
                    "E2E profile requires Azure Event Grid key: set demo.azure.event-grid.topic-key or env AZURE_EVENTGRID_TOPIC_KEY.");
        }
        if (sb.getConnectionString() == null || sb.getConnectionString().isBlank()) {
            throw new IllegalStateException(
                    "E2E profile requires Service Bus: set demo.azure.service-bus.connection-string or env AZURE_SERVICEBUS_CONNECTION_STRING.");
        }
        if (sb.getEntityName() == null || sb.getEntityName().isBlank()) {
            throw new IllegalStateException(
                    "E2E profile requires Service Bus entity name: set demo.azure.service-bus.entity-name or env AZURE_SERVICEBUS_ENTITY.");
        }

        boolean directSb = storageProperties.isForwardToServiceBus();
        log.info(
                "Pipeline simulation: Event Grid topic OK, Service Bus consumer entity={}, directAppSendToServiceBus={} "
                        + "(false = rely on Azure Event Subscription EventGrid→ServiceBus only)",
                sb.getEntityName(),
                directSb);
    }
}

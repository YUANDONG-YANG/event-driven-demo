package com.demo.azure.servicebus;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.demo.azure.config.AzureDemoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sender client used to push storage file notifications to Service Bus. Separate from the optional processor consumer.
 */
@Configuration
@ConditionalOnProperty(prefix = "demo.storage", name = "forward-to-service-bus", havingValue = "true")
public class StorageServiceBusSenderConfig {

    private static final Logger log = LoggerFactory.getLogger(StorageServiceBusSenderConfig.class);

    @Bean(destroyMethod = "close")
    public ServiceBusSenderClient storageServiceBusSender(AzureDemoProperties props) {
        var sb = props.getServiceBus();
        if (sb.getConnectionString() == null || sb.getConnectionString().isBlank()) {
            throw new IllegalStateException(
                    "demo.storage.forward-to-service-bus=true requires demo.azure.service-bus.connection-string");
        }
        if (sb.getEntityName() == null || sb.getEntityName().isBlank()) {
            throw new IllegalStateException(
                    "demo.storage.forward-to-service-bus=true requires demo.azure.service-bus.entity-name");
        }
        ServiceBusSenderClient client;
        if (sb.isTopicSubscription()) {
            client = new ServiceBusClientBuilder()
                    .connectionString(sb.getConnectionString())
                    .sender()
                    .topicName(sb.getEntityName())
                    .buildClient();
            log.info("Service Bus sender bean ready: topicName={}", sb.getEntityName());
        } else {
            client = new ServiceBusClientBuilder()
                    .connectionString(sb.getConnectionString())
                    .sender()
                    .queueName(sb.getEntityName())
                    .buildClient();
            log.info("Service Bus sender bean ready: queueName={}", sb.getEntityName());
        }
        return client;
    }
}

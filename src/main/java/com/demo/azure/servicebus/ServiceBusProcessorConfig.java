package com.demo.azure.servicebus;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.demo.azure.config.AzureDemoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "demo.azure.service-bus", name = "enabled", havingValue = "true")
public class ServiceBusProcessorConfig {

    private static final Logger log = LoggerFactory.getLogger(ServiceBusProcessorConfig.class);

    private final ServiceBusIngestMessageHandler messageHandler;

    public ServiceBusProcessorConfig(ServiceBusIngestMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Bean(destroyMethod = "close")
    public ServiceBusProcessorClient serviceBusProcessor(AzureDemoProperties props) {
        var sb = props.getServiceBus();
        var builder = new ServiceBusClientBuilder().connectionString(sb.getConnectionString()).processor();

        ServiceBusProcessorClient client;
        if (sb.isTopicSubscription()) {
            client = builder
                    .topicName(sb.getEntityName())
                    .subscriptionName(sb.getSubscriptionName())
                    .processMessage(messageHandler::onMessage)
                    .processError(messageHandler::onError)
                    .buildProcessorClient();
        } else {
            client = builder
                    .queueName(sb.getEntityName())
                    .processMessage(messageHandler::onMessage)
                    .processError(messageHandler::onError)
                    .buildProcessorClient();
        }
        client.start();
        log.info("Service Bus processor started; messages will be persisted to the database.");
        return client;
    }
}

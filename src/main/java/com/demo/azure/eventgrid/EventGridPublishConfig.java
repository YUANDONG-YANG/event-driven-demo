package com.demo.azure.eventgrid;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.models.CloudEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.demo.azure.config.AzureDemoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "demo.azure.event-grid", name = "enabled", havingValue = "true")
public class EventGridPublishConfig {

    @Bean
    public EventGridPublisherClient<CloudEvent> cloudEventPublisherClient(AzureDemoProperties props) {
        var eg = props.getEventGrid();
        return new EventGridPublisherClientBuilder()
                .endpoint(eg.getTopicEndpoint())
                .credential(new AzureKeyCredential(eg.getTopicKey()))
                .buildCloudEventPublisherClient();
    }
}

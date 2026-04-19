package com.demo.azure.eventgrid;

import com.azure.core.models.CloudEvent;
import com.azure.core.models.CloudEventDataFormat;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/event-grid")
@ConditionalOnBean(EventGridPublisherClient.class)
@Tag(name = "Event Grid", description = "Requires demo.azure.event-grid and a configured topic (hidden otherwise)")
public class EventGridDemoController {

    private final EventGridPublisherClient<CloudEvent> publisher;

    public EventGridDemoController(EventGridPublisherClient<CloudEvent> publisher) {
        this.publisher = publisher;
    }

    /** Publishes a sample CloudEvent for wiring Event Subscription → Service Bus in Azure Portal. */
    @PostMapping(value = "/publish-sample", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Publish sample CloudEvent", description = "Sends Demo.Sample.Ping to the custom Event Grid topic.")
    public Map<String, String> publishSample() {
        CloudEvent event = new CloudEvent(
                "/demo/spring-boot",
                "Demo.Sample.Ping",
                BinaryData.fromObject(Map.of("ping", UUID.randomUUID().toString(), "at", OffsetDateTime.now().toString())),
                CloudEventDataFormat.JSON,
                "application/json");
        publisher.sendEvent(event);
        return Map.of("status", "sent", "type", event.getType());
    }
}

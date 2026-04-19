package com.demo.azure.review;

import java.util.List;

/**
 * Study notes: how Event Grid and Service Bus relate (data-only for /api/review).
 */
public final class EventGridServiceBusConcept {

    private EventGridServiceBusConcept() {}

    public record Section(String title, List<String> bullets) {}

    public static List<Section> sections() {
        return List.of(
                new Section(
                        "What each service is for",
                        List.of(
                                "Event Grid: cloud-scale event routing. Sources include Azure resource lifecycle, Blob, subscription-level events, or custom topics; delivery is often HTTP/WebHook, focused on routing \"what happened where\".",
                                "Service Bus: enterprise messaging. Queues / topics with subscriptions, retries, dead-letter, sessions, scheduled messages—focused on reliable delivery and decoupling consumers.")),
                new Section(
                        "How they connect (typical pattern)",
                        List.of(
                                "Point an Event Grid subscription (Event Subscription) at a Service Bus queue or topic: source → Event Grid → Service Bus → your Spring app consumes via the Service Bus SDK.",
                                "Event Grid selects and routes sources; Service Bus provides buffering, retries, and multi-subscriber semantics.")),
                new Section(
                        "When to prefer which (rule of thumb)",
                        List.of(
                                "Many Azure event sources, filtering, and fan-out: start with Event Grid (often to Service Bus or Functions).",
                                "Strict consumer semantics (sessions, DLQ, duplicate detection): model that on Service Bus; Event Grid does not replace those features.")),
                new Section(
                        "How this sample maps to code",
                        List.of(
                                "eventgrid package: publish CloudEvents to a custom topic (endpoint + key).",
                                "servicebus package: Service Bus processor for a queue or topic subscription (connection string + entity names).",
                                "Enable either independently; in Azure Portal, wire Grid to Service Bus with an event subscription.")));
    }
}

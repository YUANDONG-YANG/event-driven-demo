package com.demo.azure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo.azure")
public class AzureDemoProperties {

    private final EventGrid eventGrid = new EventGrid();
    private final ServiceBus serviceBus = new ServiceBus();

    public EventGrid getEventGrid() {
        return eventGrid;
    }

    public ServiceBus getServiceBus() {
        return serviceBus;
    }

    public static class EventGrid {
        /** Custom topic publish URL, e.g. https://{name}.{region}-1.eventgrid.azure.net/api/events */
        private String topicEndpoint = "";
        private String topicKey = "";
        private boolean enabled = false;

        public String getTopicEndpoint() {
            return topicEndpoint;
        }

        public void setTopicEndpoint(String topicEndpoint) {
            this.topicEndpoint = topicEndpoint;
        }

        public String getTopicKey() {
            return topicKey;
        }

        public void setTopicKey(String topicKey) {
            this.topicKey = topicKey;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class ServiceBus {
        private String connectionString = "";
        /** Queue name, or topic name when using a subscription */
        private String entityName = "";
        /** Subscription name when consuming a topic; leave empty for a queue */
        private String subscriptionName = "";
        private boolean enabled = false;

        public String getConnectionString() {
            return connectionString;
        }

        public void setConnectionString(String connectionString) {
            this.connectionString = connectionString;
        }

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public String getSubscriptionName() {
            return subscriptionName;
        }

        public void setSubscriptionName(String subscriptionName) {
            this.subscriptionName = subscriptionName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isTopicSubscription() {
            return subscriptionName != null && !subscriptionName.isBlank();
        }
    }
}

package com.demo.azure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "demo.storage")
public class StorageProperties {

    /**
     * Root directory to watch (absolute or relative to user.dir). For a packaged JAR, point to a writable path
     * (e.g. {@code STORAGE_ROOT}).
     */
    private String root = "";

    private boolean watchEnabled = true;

    /** Create the root and today's yyyy-MM-dd folder on startup. */
    private boolean ensureTodayFolderOnStartup = true;

    /**
     * When a new file appears under storage, publish a CloudEvent to the configured Event Grid custom topic.
     * Requires {@code demo.azure.event-grid.enabled=true} and topic credentials.
     */
    private boolean forwardToEventGrid = false;

    /**
     * When a new file appears, send a message to Service Bus (queue or topic). Requires connection string and entity
     * name; use {@code demo.storage.forward-to-service-bus=true}.
     */
    private boolean forwardToServiceBus = false;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public boolean isWatchEnabled() {
        return watchEnabled;
    }

    public void setWatchEnabled(boolean watchEnabled) {
        this.watchEnabled = watchEnabled;
    }

    public boolean isEnsureTodayFolderOnStartup() {
        return ensureTodayFolderOnStartup;
    }

    public void setEnsureTodayFolderOnStartup(boolean ensureTodayFolderOnStartup) {
        this.ensureTodayFolderOnStartup = ensureTodayFolderOnStartup;
    }

    public boolean isForwardToEventGrid() {
        return forwardToEventGrid;
    }

    public void setForwardToEventGrid(boolean forwardToEventGrid) {
        this.forwardToEventGrid = forwardToEventGrid;
    }

    public boolean isForwardToServiceBus() {
        return forwardToServiceBus;
    }

    public void setForwardToServiceBus(boolean forwardToServiceBus) {
        this.forwardToServiceBus = forwardToServiceBus;
    }
}

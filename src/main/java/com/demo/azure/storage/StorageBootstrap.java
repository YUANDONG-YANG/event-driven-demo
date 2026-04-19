package com.demo.azure.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class StorageBootstrap {

    private static final Logger log = LoggerFactory.getLogger(StorageBootstrap.class);
    private static final DateTimeFormatter DATE_DIR = DateTimeFormatter.ISO_LOCAL_DATE;

    private final StorageProperties properties;
    private final StoragePathProvider paths;

    public StorageBootstrap(StorageProperties properties, StoragePathProvider paths) {
        this.properties = properties;
        this.paths = paths;
    }

    @Order(0)
    @EventListener(ApplicationReadyEvent.class)
    public void prepareDirectories() throws IOException {
        Path root = paths.root();
        Files.createDirectories(root);
        log.info(
                "Storage forwarding flags: forwardToEventGrid={} forwardToServiceBus={}",
                properties.isForwardToEventGrid(),
                properties.isForwardToServiceBus());
        if (properties.isEnsureTodayFolderOnStartup()) {
            Path today = root.resolve(LocalDate.now().format(DATE_DIR));
            Files.createDirectories(today);
            log.info("Storage root: {}, ensured today folder: {}", root.toAbsolutePath(), today.getFileName());
        } else {
            log.info("Storage root: {}", root.toAbsolutePath());
        }
    }
}

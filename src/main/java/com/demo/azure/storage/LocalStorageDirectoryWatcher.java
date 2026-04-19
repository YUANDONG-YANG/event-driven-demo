package com.demo.azure.storage;

import com.demo.azure.trace.PipelineTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Watches {@link StoragePathProvider#root()} recursively. Azure Event Grid cannot bind to a local path; this is the
 * local trigger that downstream components use to publish to Event Grid / Service Bus.
 */
@Component
@ConditionalOnProperty(prefix = "demo.storage", name = "watch-enabled", havingValue = "true", matchIfMissing = true)
public class LocalStorageDirectoryWatcher {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageDirectoryWatcher.class);

    private final StoragePathProvider paths;
    private final ApplicationEventPublisher eventPublisher;

    private WatchService watchService;
    private ExecutorService executor;
    private final ConcurrentHashMap<WatchKey, Path> keyToDir = new ConcurrentHashMap<>();

    public LocalStorageDirectoryWatcher(StoragePathProvider paths, ApplicationEventPublisher eventPublisher) {
        this.paths = paths;
        this.eventPublisher = eventPublisher;
    }

    @Order(100)
    @EventListener(ApplicationReadyEvent.class)
    public void start() throws IOException {
        Path root = paths.root();
        if (!Files.isDirectory(root)) {
            log.warn("Storage root does not exist, skipping watch: {}", root);
            return;
        }
        watchService = FileSystems.getDefault().newWatchService();
        registerTree(root);
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "local-storage-watch");
            t.setDaemon(true);
            return t;
        });
        executor.submit(this::pollLoop);
        log.info(
                "Watching storage directory (Event Grid does not support local disks; this simulates file events): {}",
                root.toAbsolutePath());
    }

    private void registerTree(Path start) throws IOException {
        registerDirectory(start);
        if (!Files.isDirectory(start)) {
            return;
        }
        try (Stream<Path> walk = Files.list(start)) {
            walk.filter(Files::isDirectory).forEach(p -> {
                try {
                    registerTree(p);
                } catch (IOException e) {
                    log.warn("Failed to register subdirectory: {}", p, e);
                }
            });
        }
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE);
        keyToDir.put(key, dir);
    }

    private void pollLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            Path dir = keyToDir.get(key);
            if (dir == null) {
                if (!key.reset()) {
                    break;
                }
                continue;
            }
            for (WatchEvent<?> ev : key.pollEvents()) {
                if (ev.kind() == OVERFLOW) {
                    log.warn("WatchService OVERFLOW: {}", dir);
                    continue;
                }
                @SuppressWarnings("unchecked")
                WatchEvent<Path> pathEv = (WatchEvent<Path>) ev;
                Path name = pathEv.context();
                Path child = dir.resolve(name);
                try {
                    if (Files.isDirectory(child)) {
                        registerTree(child);
                    } else if (shouldEmitFileEvent(child)) {
                        fireFileDetected(child);
                    }
                } catch (IOException e) {
                    log.warn("Failed to handle new path: {}", child, e);
                }
            }
            if (!key.reset()) {
                keyToDir.remove(key);
            }
        }
    }

    private static boolean shouldEmitFileEvent(Path p) {
        if (p == null || p.getFileName() == null) {
            return false;
        }
        String n = p.getFileName().toString();
        if (n.equalsIgnoreCase(".gitkeep")) {
            log.debug("Ignoring .gitkeep: {}", p);
            return false;
        }
        return true;
    }

    private void fireFileDetected(Path file) {
        Path abs = file.toAbsolutePath().normalize();
        Instant at = Instant.now();
        String traceId = UUID.randomUUID().toString();
        PipelineTrace.put(traceId);
        try {
            log.info("[pipeline=1_STORAGE_WATCH] new file path={} detectedAt={}", abs, at);
            eventPublisher.publishEvent(new StorageFileDetectedEvent(abs, at, traceId));
            log.debug("[pipeline=1_STORAGE_WATCH] event published traceId={}", traceId);
        } finally {
            PipelineTrace.clear();
        }
    }

    @PreDestroy
    public void shutdown() {
        if (executor != null) {
            executor.shutdownNow();
        }
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException ignored) {
                // ignore
            }
        }
    }
}

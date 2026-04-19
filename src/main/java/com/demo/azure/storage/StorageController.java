package com.demo.azure.storage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/storage")
@Tag(name = "Local storage", description = "Date-partitioned XML folders and storage root path")
public class StorageController {

    private final StoragePathProvider paths;
    private final StorageProperties properties;

    public StorageController(StoragePathProvider paths, StorageProperties properties) {
        this.paths = paths;
        this.properties = properties;
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Storage layout",
            description = "Returns the storage root path and yyyy-MM-dd subdirectories.")
    public StorageInfoResponse info() throws IOException {
        Path root = paths.root();
        List<String> dateFolders;
        if (!Files.isDirectory(root)) {
            dateFolders = List.of();
        } else {
            try (Stream<Path> s = Files.list(root)) {
                dateFolders = s.filter(Files::isDirectory)
                        .map(p -> p.getFileName().toString())
                        .sorted(Comparator.reverseOrder())
                        .toList();
            }
        }
        return new StorageInfoResponse(
                root.toString(),
                properties.isWatchEnabled(),
                properties.isForwardToEventGrid(),
                properties.isForwardToServiceBus(),
                dateFolders);
    }

    public record StorageInfoResponse(
            String rootPath,
            boolean localWatchEnabled,
            boolean forwardToEventGrid,
            boolean forwardToServiceBus,
            List<String> dateFolders) {}
}

package com.demo.azure.storage;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class StoragePathProvider {

    private final Path root;

    public StoragePathProvider(StorageProperties properties) {
        String configured = properties.getRoot();
        if (configured != null && !configured.isBlank()) {
            root = Paths.get(configured).toAbsolutePath().normalize();
        } else {
            root = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "storage")
                    .toAbsolutePath()
                    .normalize();
        }
    }

    public Path root() {
        return root;
    }
}

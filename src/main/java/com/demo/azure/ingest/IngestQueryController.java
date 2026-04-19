package com.demo.azure.ingest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingest")
@Tag(name = "Ingest", description = "Rows persisted from Service Bus consumption")
public class IngestQueryController {

    private final StorageFileEventRepository repository;

    public IngestQueryController(StorageFileEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping(value = "/storage-events", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Last 50 ingested storage file events", description = "Verifies DB persistence after Service Bus consumer runs.")
    public List<StorageFileEventEntity> recent() {
        return repository.findTop50ByOrderByPersistedAtDesc();
    }
}

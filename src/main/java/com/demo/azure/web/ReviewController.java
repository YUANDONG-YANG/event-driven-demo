package com.demo.azure.web;

import com.demo.azure.review.EventGridServiceBusConcept;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Study notes", description = "Event Grid vs Service Bus (no Azure required)")
public class ReviewController {

    @GetMapping(value = "/review", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get study notes", description = "JSON overview of concepts and how the services connect.")
    public Map<String, Object> review() {
        return Map.of(
                "springBoot", "3.2.x",
                "topic", "Relationship between Azure Event Grid and Service Bus",
                "sections", EventGridServiceBusConcept.sections());
    }
}

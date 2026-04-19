package com.demo.azure.storage.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/storage/virtual-orders")
@Tag(name = "Virtual order XML", description = "Generate a virtual order and save XML under the date folder")
public class VirtualOrderController {

    private final VirtualOrderService virtualOrderService;

    public VirtualOrderController(VirtualOrderService virtualOrderService) {
        this.virtualOrderService = virtualOrderService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create virtual order XML", description = "Writes a UTF-8 XML file under storage/{yyyy-MM-dd}/.")
    public ResponseEntity<VirtualOrderResponse> create(@Valid @RequestBody VirtualOrderRequest request)
            throws IOException {
        VirtualOrderResponse body = virtualOrderService.createVirtualOrderXml(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}

package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.models.dtos.ModelDetailsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
@Tag(name = "Models", description = "API for managing car models")
@SecurityRequirement(name = "bearerAuth")
public class ModelController {

    private final ModelService service;


    @GetMapping("/{id}")
    @Operation(summary = "Get model details with a list of generations")
    public ResponseEntity<ModelDetailsResponse> getModelsByBrandId(
            @Parameter(description = "ID of the model") @PathVariable UUID id
    ) {
        ModelDetailsResponse model = service.getModelDetailsById(id);

        return ResponseEntity.ok(model);
    }
}

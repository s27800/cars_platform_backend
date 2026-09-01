package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.generations.dtos.GenerationDetailsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/generations")
@RequiredArgsConstructor
@Tag(name = "Generations", description = "API for managing car generations")
@SecurityRequirement(name = "bearerAuth")
public class GenerationController {

    private final GenerationService service;


    @GetMapping("/{id}")
    @Operation(summary = "Get generation details with a list of cars")
    public ResponseEntity<GenerationDetailsResponse> getGenerationDetailsById(
            @Parameter(description = "ID of the generation") @PathVariable UUID id
    ) {
        GenerationDetailsResponse generation = service.getGenerationDetailsById(id);

        return ResponseEntity.ok(generation);
    }
}

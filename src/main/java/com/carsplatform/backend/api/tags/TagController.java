package com.carsplatform.backend.api.tags;

import com.carsplatform.backend.api.tags.dtos.CarTagResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Api for managing tags")
public class TagController {
    private final TagService service;

    @GetMapping
    @Operation(summary = "Get all available tags")
    public ResponseEntity<List<CarTagResponse>> getAllTags() {
        return ResponseEntity.ok(service.getAllTags());
    }
}

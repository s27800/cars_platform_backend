package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.bodyType.dtos.CarBodyTypeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/body-types")
@RequiredArgsConstructor
@Tag(name = "Body Types", description = "Api for managing body types")
public class BodyTypeController {

    private final BodyTypeService service;


    @GetMapping
    @Operation(summary = "Get all available body types")
    public ResponseEntity<List<CarBodyTypeResponse>> getAllBodyTypes() {
        return ResponseEntity.ok(service.getAllBodyTypes());
    }
}

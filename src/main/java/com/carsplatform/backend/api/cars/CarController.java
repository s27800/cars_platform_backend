package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Tag(name = "Cars", description = "API for managing cars")
@SecurityRequirement(name = "bearerAuth")
public class CarController {
    private final CarService carService;

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed information about a car")
    public ResponseEntity<CarDetailsResponse> getCarDetailsById(@Parameter(description = "ID of the car to retrieve") @PathVariable Integer id, Pageable pageable) {
        CarDetailsResponse carDetailsResponse = carService.getCarDetailsForCarId(id, pageable);
        return ResponseEntity.ok(carDetailsResponse);
    }
}

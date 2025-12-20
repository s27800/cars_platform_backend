package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;
import com.carsplatform.backend.api.cars.dtos.CarsListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Tag(name = "Cars", description = "API for managing cars")
@SecurityRequirement(name = "bearerAuth")
public class CarController {
    private final CarService carService;

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed information about a car")
    public ResponseEntity<CarDetailsResponse> getCarDetailsById(@Parameter(description = "ID of the car to retrieve") @PathVariable Integer id) {
        CarDetailsResponse carDetailsResponse = carService.getCarDetailsForCarId(id);
        return ResponseEntity.ok(carDetailsResponse);
    }

    @GetMapping("/search")
    @Operation(summary = "Search and sort cars by filters")
    public ResponseEntity<Page<CarsListResponse>> searchCars(
            @RequestParam(required = false) List<Integer> brandIds,
            @RequestParam(required = false) List<Integer> modelIds,
            @RequestParam(required = false) List<Integer> generationIds,
            @RequestParam(required = false) List<Integer> bodyTypeIds,
            @RequestParam(required = false) List<Integer> tagIds,
            @RequestParam(required = false) Integer minDisplacement,
            @RequestParam(required = false) Integer maxDisplacement,
            @RequestParam(required = false) List<String> engineTypes,
            @RequestParam(required = false) Integer minPower,
            @RequestParam(required = false) Integer maxPower,
            @RequestParam(required = false) Integer minTorque,
            @RequestParam(required = false) Integer maxTorque,
            @RequestParam(required = false) List<String> drives,
            @RequestParam(required = false) List<String> transmissionTypes,
            @RequestParam(required = false) Integer minSpeed,
            @RequestParam(required = false) Integer maxSpeed,
            @RequestParam(required = false) Double minFuelConsumptionMixed,
            @RequestParam(required = false) Double maxFuelConsumptionMixed,
            Pageable pageable
    ) {

        Page<CarsListResponse> response = carService.searchCars(
                brandIds, modelIds, generationIds, bodyTypeIds,
                tagIds, minDisplacement, maxDisplacement, engineTypes,
                minPower, maxPower, minTorque, maxTorque, drives,
                transmissionTypes, minSpeed, maxSpeed, minFuelConsumptionMixed,
                maxFuelConsumptionMixed, pageable);

        return ResponseEntity.ok(response);
    }
}

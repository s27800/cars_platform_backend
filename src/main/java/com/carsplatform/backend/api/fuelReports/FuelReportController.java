package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fuel-reports")
@RequiredArgsConstructor
@Tag(name = "Fuel-Reports", description = "API for managing fuel reports")
@SecurityRequirement(name = "bearerAuth")
public class FuelReportController {
    private final FuelReportService fuelReportService;

    @GetMapping("/{id}")
    @Operation(summary = "Get average fuel consumption for car")
    public ResponseEntity<AverageFuelConsumptionResponse> getAverageFuelConsumptionByCarId(
            @Parameter(description = "ID of the car to retrieve average duel consumption")
            @PathVariable Integer id) {

        AverageFuelConsumptionResponse response = fuelReportService.getAverageFuelConsumptionForCar(id);
        return ResponseEntity.ok(response);
    }
}

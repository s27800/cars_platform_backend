package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;
import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/fuel-reports")
@RequiredArgsConstructor
@Tag(name = "Fuel Reports", description = "API for managing fuel reports")
@SecurityRequirement(name = "bearerAuth")
public class FuelReportController {

    private final FuelReportService service;


    @GetMapping("/{carId}/average-consumption")
    @Operation(summary = "Get average fuel consumption for a car")
    public ResponseEntity<AverageFuelConsumptionResponse> getAverageFuelConsumptionByCarId(
            @Parameter(description = "ID of the car")
            @PathVariable UUID carId
    ) {
        AverageFuelConsumptionResponse response = service.getAverageFuelConsumptionForCar(carId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{carId}")
    @Operation(summary = "Get fuel consumption reports for a car")
    public ResponseEntity<Page<FuelReportResponse>> getFuelReports(
            @Parameter(description = "ID of the car") @PathVariable UUID carId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.getFuelReportsForCarId(carId, pageable));
    }

    @PostMapping("/{carId}")
    @Operation(summary = "Add new fuel consumption report for a car")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> createFuelReport(
            @Parameter(description = "ID of the car") @PathVariable UUID carId,
            @Valid @RequestBody CreateFuelReportRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        service.createFuelReport(carId, request, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{fuelReportId}")
    @Operation(summary = "Delete own fuel report")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteFuelReport(
            @Parameter(description = "ID of the fuel report to delete") @PathVariable UUID fuelReportId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        service.deleteOwnFuelReport(fuelReportId, userDetails.getUsername());

        return ResponseEntity.noContent().build();
    }
}

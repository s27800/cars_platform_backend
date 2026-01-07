package com.carsplatform.backend.api.fuelReports.dtos;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFuelReportRequest {

    @NotNull(message = "Fuel consumption value is required")
    @Positive(message = "Fuel consumption must be positive")
    @Digits(integer = 2, fraction = 1, message = "Format must be XX.X (e.g. 7.5)")
    private BigDecimal fuelConsumption;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;
}

package com.carsplatform.backend.api.performances.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarPerformanceResponse {
    private UUID id;
    private Integer maxSpeed;
    private BigDecimal acceleration0100;
    private BigDecimal acceleration100200;
    private Integer fuelTankCapacity;
    private BigDecimal fuelConsumptionCity;
    private BigDecimal fuelConsumptionRoute;
    private BigDecimal fuelConsumptionMixed;
    private Integer rangeCity;
    private Integer rangeRoute;
    private Integer rangeMixed;
    private Integer emissionCo2;
    private String fuelEmissionNorm;
}

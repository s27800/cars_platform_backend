package com.carsplatform.backend.api.engines.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarEngineResponse {
    private UUID id;
    private String engineCode;
    private String productionYears;
    private Integer displacement;
    private String engineType;
    private Integer maxPower;
    private Integer maxPowerRotationSpeed;
    private String turbo;
    private Integer cylindersNumber;
    private String cylindersLayout;
    private Integer valvesNumber;
    private String ignition;
    private String injectionType;
    private Integer maxTorque;
    private Integer maxTorqueRotationSpeed;
}

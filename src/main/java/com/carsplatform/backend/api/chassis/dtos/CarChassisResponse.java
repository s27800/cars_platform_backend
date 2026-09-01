package com.carsplatform.backend.api.chassis.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarChassisResponse {
    private UUID id;
    private String basicRims;
    private String optionalRims;
    private String basicTires;
    private String optionalTires;
    private String frontBrakes;
    private String backBrakes;
    private Integer frontBrakesRadius;
    private Integer backBrakesRadius;
    private Integer frontBrakesThickness;
    private Integer backBrakesThickness;
    private String suspension;
    private String drive;
}

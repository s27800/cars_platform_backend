package com.carsplatform.backend.api.insideDimensions.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarInsideDimensionsResponse {
    private Integer id;
    private Integer heightFromSeatToRoofFront;
    private Integer heightFromSeatToRoofBack;
    private Integer maxTrunkSpace;
    private Integer minTrunkSpace;
    private Integer minTrunkLength;
    private Integer maxTrunkLength;
    private Integer trunkWidth;
    private Integer trunkHeight;
}

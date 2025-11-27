package com.carsplatform.backend.api.outsideDimensions.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarOutsideDimensionsResponse {
    private Integer id;
    private Integer length;
    private Integer height;
    private Integer width;
    private Integer widthWithMirrors;
    private Integer heightWithOpenTrunk;
    private Integer wheelBase;
    private Integer wheelBaseFront;
    private Integer wheelBaseBack;
    private Integer overhangFront;
    private Integer overhangBack;
    private Integer clearance;
    private Integer maxRoofLoad;
}

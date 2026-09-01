package com.carsplatform.backend.api.engines.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarsListEngineResponse {
    private String engineType;
    private Integer displacement;
    private Integer maxPower;
}

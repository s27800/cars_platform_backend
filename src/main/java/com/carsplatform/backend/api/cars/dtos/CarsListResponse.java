package com.carsplatform.backend.api.cars.dtos;

import com.carsplatform.backend.api.bodyType.dtos.CarsListBodyTypeResponse;
import com.carsplatform.backend.api.engines.dtos.CarsListEngineResponse;

import com.carsplatform.backend.api.transmissions.dtos.CarsListTransmissionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarsListResponse {
    private Integer id;
    private String name;
    private String productionYears;
    private CarsListEngineResponse engine;
    private CarsListBodyTypeResponse bodyType;
    private CarsListTransmissionResponse transmission;
    private String imageUrl;
}

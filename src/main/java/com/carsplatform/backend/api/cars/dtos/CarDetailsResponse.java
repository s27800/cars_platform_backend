package com.carsplatform.backend.api.cars.dtos;

import com.carsplatform.backend.api.bodyType.dtos.CarBodyTypeResponse;
import com.carsplatform.backend.api.brands.dtos.CarBrandResponse;
import com.carsplatform.backend.api.carImages.dtos.CarImageResponse;
import com.carsplatform.backend.api.chassis.dtos.CarChassisResponse;
import com.carsplatform.backend.api.engines.dtos.CarEngineResponse;
import com.carsplatform.backend.api.generations.dtos.CarGenerationResponse;
import com.carsplatform.backend.api.insideDimensions.dtos.CarInsideDimensionsResponse;
import com.carsplatform.backend.api.models.dtos.CarModelResponse;
import com.carsplatform.backend.api.outsideDimensions.dtos.CarOutsideDimensionsResponse;
import com.carsplatform.backend.api.performances.dtos.CarPerformanceResponse;
import com.carsplatform.backend.api.tags.dtos.CarTagResponse;
import com.carsplatform.backend.api.transmissions.dtos.CarTransmissionResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CarDetailsResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer doorsNumber;
    private Integer seatsNumber;
    private String productionYears;
    private CarBrandResponse brand;
    private CarModelResponse model;
    private CarGenerationResponse generation;
    private CarBodyTypeResponse bodyType;
    private CarEngineResponse engine;
    private CarChassisResponse chassis;
    private CarTransmissionResponse transmission;
    private CarPerformanceResponse performance;
    private CarInsideDimensionsResponse insideDimensions;
    private CarOutsideDimensionsResponse outsideDimensions;
    private List<CarImageResponse> images;
    private Set<CarTagResponse> tags;
}

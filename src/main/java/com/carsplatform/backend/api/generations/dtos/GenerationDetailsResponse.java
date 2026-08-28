package com.carsplatform.backend.api.generations.dtos;

import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;
import com.carsplatform.backend.api.cars.dtos.CarsListResponse;
import com.carsplatform.backend.api.models.dtos.ModelsListResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationDetailsResponse {
    private UUID id;
    private String name;
    private ModelsListResponse model;
    private BrandsListResponse brand;
    private List<CarsListResponse> cars;
}

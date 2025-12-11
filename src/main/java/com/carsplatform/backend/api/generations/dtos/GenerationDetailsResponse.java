package com.carsplatform.backend.api.generations.dtos;

import com.carsplatform.backend.api.cars.dtos.CarsListResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationDetailsResponse {
    private Integer id;
    private String name;
    private List<CarsListResponse> cars;
}

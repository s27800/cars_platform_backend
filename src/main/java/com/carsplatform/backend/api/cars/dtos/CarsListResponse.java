package com.carsplatform.backend.api.cars.dtos;

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
}

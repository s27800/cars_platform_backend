package com.carsplatform.backend.api.bodyType.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarsListBodyTypeResponse {
    private String name;
}

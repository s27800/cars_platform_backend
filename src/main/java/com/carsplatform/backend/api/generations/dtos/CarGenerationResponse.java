package com.carsplatform.backend.api.generations.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarGenerationResponse {
    private Integer id;
    private String name;
}

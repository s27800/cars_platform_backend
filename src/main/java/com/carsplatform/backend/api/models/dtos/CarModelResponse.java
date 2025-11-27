package com.carsplatform.backend.api.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CarModelResponse {
    private Integer id;
    private String name;
    private String description;
}

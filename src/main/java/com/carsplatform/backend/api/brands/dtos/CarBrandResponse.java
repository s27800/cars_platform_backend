package com.carsplatform.backend.api.brands.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarBrandResponse {
    private UUID id;
    private String name;
    private String country;
    private Integer foundedYear;
    private String description;
    private String logoUrl;
}

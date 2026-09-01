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
public class BrandsListResponse {
    private UUID id;
    private String name;
    private String logoUrl;
}

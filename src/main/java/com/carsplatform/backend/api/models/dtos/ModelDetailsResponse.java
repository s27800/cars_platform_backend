package com.carsplatform.backend.api.models.dtos;

import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;
import com.carsplatform.backend.api.generations.dtos.GenerationsListResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDetailsResponse {
    private Integer id;
    private String name;
    private String description;
    private BrandsListResponse brand;
    private List<GenerationsListResponse> generations;
}

package com.carsplatform.backend.api.brands.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandsListResponse {
    private Integer id;
    private String name;
}

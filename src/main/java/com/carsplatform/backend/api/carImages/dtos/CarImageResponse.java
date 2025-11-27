package com.carsplatform.backend.api.carImages.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarImageResponse {
    private Integer id;
    private String imageUrl;
    private Boolean isMain;
}

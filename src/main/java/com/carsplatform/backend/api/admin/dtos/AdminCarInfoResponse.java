package com.carsplatform.backend.api.admin.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCarInfoResponse {
    private Integer carId;
    private String carName;
    private String brandName;
    private String modelName;
    private String generationName;
}

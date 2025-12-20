package com.carsplatform.backend.api.transmissions.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarsListTransmissionResponse {
    private String transmissionType;
}

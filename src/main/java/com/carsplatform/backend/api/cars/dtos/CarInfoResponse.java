package com.carsplatform.backend.api.cars.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


/**
 * Short description of a car, for responses that only have to say which car they concern
 * instead of carrying its whole specification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarInfoResponse {
    private UUID carId;
    private String carName;
    private String brandName;
    private String modelName;
    private String generationName;
}

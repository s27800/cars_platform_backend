package com.carsplatform.backend.api.transmissions.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarTransmissionResponse {
    private Integer id;
    private String transmissionType;
    private String transmissionName;
    private Integer gearsNumber;
    private String clutchType;
}

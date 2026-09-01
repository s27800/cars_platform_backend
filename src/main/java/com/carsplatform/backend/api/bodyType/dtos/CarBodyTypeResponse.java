package com.carsplatform.backend.api.bodyType.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarBodyTypeResponse {
    private UUID id;
    private String name;
}

package com.carsplatform.backend.api.tags.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarTagResponse {
    private Integer id;
    private String name;
}

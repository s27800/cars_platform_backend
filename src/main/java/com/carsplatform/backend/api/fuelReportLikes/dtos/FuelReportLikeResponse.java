package com.carsplatform.backend.api.fuelReportLikes.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelReportLikeResponse {
    private boolean isLiked;
    private long likesCount;
}

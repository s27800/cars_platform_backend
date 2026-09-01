package com.carsplatform.backend.api.reviews.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AverageRatingsResponse {
    private Double avgEngineRating;
    private Double avgTransmissionRating;
    private Double avgSteeringRating;
    private Double avgSuspensionRating;
    private Double avgVisibilityRating;
    private Double avgErgonomicsRating;
    private Double avgSoundProofingRating;
    private Double avgInteriorSpaceRating;
    private Double avgMaintenanceRating;
    private Double avgPriceQualityRating;
    private Double avgFailureFreeRating;
}

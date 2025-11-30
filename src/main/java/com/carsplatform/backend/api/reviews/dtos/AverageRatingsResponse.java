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
    private Integer avgEngineRating;
    private Integer avgTransmissionRating;
    private Integer avgSteeringRating;
    private Integer avgSuspensionRating;
    private Integer avgVisibilityRating;
    private Integer avgErgonomicsRating;
    private Integer avgSoundProofingRating;
    private Integer avgInteriorSpaceRating;
    private Integer avgMaintenanceRating;
    private Integer avgPriceQualityRating;
    private Integer avgFailureFreeRating;
}

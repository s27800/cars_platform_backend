package com.carsplatform.backend.api.reviews.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotBlank(message = "Comment is required")
    @Size(min = 10, max = 2000, message = "Comment must be between 10 and 2000 characters")
    private String comment;

    @NotNull(message = "Engine rating is required")
    @Min(1) @Max(5)
    private Integer engineRating;

    @NotNull(message = "Transmission rating is required")
    @Min(1) @Max(5)
    private Integer transmissionRating;

    @NotNull(message = "Steering rating is required")
    @Min(1) @Max(5)
    private Integer steeringRating;

    @NotNull(message = "Suspension rating is required")
    @Min(1) @Max(5)
    private Integer suspensionRating;

    @NotNull(message = "Visibility rating is required")
    @Min(1) @Max(5)
    private Integer visibilityRating;

    @NotNull(message = "Ergonomics rating is required")
    @Min(1) @Max(5)
    private Integer ergonomicsRating;

    @NotNull(message = "Sound proofing rating is required")
    @Min(1) @Max(5)
    private Integer soundProofingRating;

    @NotNull(message = "Interior space rating is required")
    @Min(1) @Max(5)
    private Integer interiorSpaceRating;

    @NotNull(message = "Maintenance rating is required")
    @Min(1) @Max(5)
    private Integer maintenanceRating;

    @NotNull(message = "Price/Quality rating is required")
    @Min(1) @Max(5)
    private Integer priceQualityRating;

    @NotNull(message = "Failure free rating is required")
    @Min(1) @Max(5)
    private Integer failureFreeRating;
}

package com.carsplatform.backend.api.reviews.dtos;

import com.carsplatform.backend.api.users.dtos.UsernameResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String comment;
    private Integer engineRating;
    private Integer transmissionRating;
    private Integer steeringRating;
    private Integer suspensionRating;
    private Integer visibilityRating;
    private Integer ergonomicsRating;
    private Integer soundProofingRating;
    private Integer interiorSpaceRating;
    private Integer maintenanceRating;
    private Integer priceQualityRating;
    private Integer failureFreeRating;
    private LocalDateTime reviewDate;
    private Boolean isApproved;
    private UsernameResponse usernameResponse;
}

package com.carsplatform.backend.api.reviews.dtos;

import com.carsplatform.backend.api.users.dtos.UsernameResponse;
import com.carsplatform.backend.common.ModerationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private UUID id;
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
    private ModerationStatus status;
    private Long likesCount;
    private UsernameResponse usernameResponse;
}

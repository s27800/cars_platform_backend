package com.carsplatform.backend.api.reviews.dtos;

import com.carsplatform.backend.api.users.dtos.UsernameResponse;

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
    private Double engineRating;
    private Double transmissionRating;
    private Double steeringRating;
    private Double suspensionRating;
    private Double visibilityRating;
    private Double ergonomicsRating;
    private Double soundProofingRating;
    private Double interiorSpaceRating;
    private Double maintenanceRating;
    private Double priceQualityRating;
    private Double failureFreeRating;
    private LocalDateTime reviewDate;
    private Boolean isApproved;
    private Long likesCount;
    private UsernameResponse usernameResponse;
}

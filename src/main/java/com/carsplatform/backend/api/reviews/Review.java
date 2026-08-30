package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "review")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    @JsonIgnore
    private Car car;

    @Column(name = "comm", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Comment cannot be null")
    private String comment;

    @Column(name = "engine_rating", nullable = false)
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer engineRating;

    @Column(name = "transmission_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer transmissionRating;

    @Column(name = "steering_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer steeringRating;

    @Column(name = "suspension_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer suspensionRating;

    @Column(name = "visibility_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer visibilityRating;

    @Column(name = "ergonomics_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer ergonomicsRating;

    @Column(name = "sound_proofing_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer soundProofingRating;

    @Column(name = "interior_space_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer interiorSpaceRating;

    @Column(name = "maintenance_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer maintenanceRating;

    @Column(name = "price_quality_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer priceQualityRating;

    @Column(name = "failure_free_rating", nullable = false)
    @Min(value = 1)
    @Max(value = 5)
    private Integer failureFreeRating;

    @Column(name = "review_date", updatable = false)
    private LocalDateTime reviewDate;

    @Column(name = "is_approved")
    @Builder.Default
    private Boolean isApproved = false;

    @Formula("(SELECT COUNT(*) FROM likes l WHERE l.review_id = id)")
    private Long likesCount;

    @PrePersist
    protected void onCreate() {
        reviewDate = LocalDateTime.now();
    }
}

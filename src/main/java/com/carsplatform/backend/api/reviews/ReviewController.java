package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;
import com.carsplatform.backend.api.reviews.dtos.CreateReviewRequest;
import com.carsplatform.backend.api.reviews.dtos.ReviewResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "API for managing car reviews")
public class ReviewController {

    private final ReviewService service;

    @GetMapping("/{carId}/average-ratings")
    @Operation(summary = "Get average ratings for a car")
    public ResponseEntity<AverageRatingsResponse> getAverageRatingsForCar(
            @Parameter(description = "ID of the car to get ratings for")
            @PathVariable UUID carId) {

        AverageRatingsResponse averageRatings = service.getAverageRatingsForCar(carId);

        return ResponseEntity.ok(averageRatings);
    }

    @GetMapping("/{carId}")
    @Operation(summary = "Get reviews for a car")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable UUID carId,
            Pageable pageable) {

        return ResponseEntity.ok(service.getReviewsForCarId(carId, pageable));
    }

    @PostMapping("/{carId}")
    @Operation(summary = "Add new review for a car")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> createReview(
            @Parameter(description = "ID of the car") @PathVariable UUID carId,
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        service.createReview(carId, request, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

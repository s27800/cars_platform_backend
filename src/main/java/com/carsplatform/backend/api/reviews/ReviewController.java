package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "API for managing car reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{carId}/average-ratings")
    @Operation(summary = "Get average ratings for a car")
    public ResponseEntity<AverageRatingsResponse> getAverageRatingsForCar(
            @Parameter(description = "ID of the car to get ratings for")
            @PathVariable Integer carId) {

        AverageRatingsResponse averageRatings = reviewService.getAverageRatingsForCar(carId);

        return ResponseEntity.ok(averageRatings);
    }
}

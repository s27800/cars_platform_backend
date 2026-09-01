package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.reviews.dtos.ReviewDetailsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
@Tag(name = "Admin Reviews", description = "Admin API for managing reviews moderation")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {

    private final AdminReviewService adminReviewService;


    @GetMapping("/pending")
    @Operation(summary = "Get all pending reviews waiting for approval")
    public ResponseEntity<Page<ReviewDetailsResponse>> getPendingReviews(Pageable pageable) {
        return ResponseEntity.ok(adminReviewService.getPendingReviews(pageable));
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve or reject a review")
    public ResponseEntity<Void> approveReview(
            @Parameter(description = "ID of the review") @PathVariable UUID id,
            @Parameter(description = "Whether to approve (true) or reject (false) the review")
            @RequestParam boolean approve
    ) {
        adminReviewService.approveReview(id, approve);

        return ResponseEntity.noContent().build();
    }
}

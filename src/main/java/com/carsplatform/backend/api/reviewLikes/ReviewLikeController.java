package com.carsplatform.backend.api.reviewLikes;

import com.carsplatform.backend.common.LikeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/likes/review")
@RequiredArgsConstructor
@Tag(name = "Review Likes", description = "API for managing review likes")
@SecurityRequirement(name = "bearerAuth")
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;


    @PostMapping("/{reviewId}")
    @Operation(summary = "Toggle like for a review")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(reviewLikeService.toggleLike(reviewId, userDetails.getUsername()));
    }

    @GetMapping("/{reviewId}/status")
    @Operation(summary = "Get like status and count for a review")
    public ResponseEntity<LikeResponse> getLikeStatus(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(reviewLikeService.getLikeStatus(reviewId, userDetails.getUsername()));
    }
}

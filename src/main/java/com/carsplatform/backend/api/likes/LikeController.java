package com.carsplatform.backend.api.likes;

import com.carsplatform.backend.api.likes.dtos.LikeResponse;

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
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@Tag(name = "Likes", description = "API for managing review likes")
@SecurityRequirement(name = "bearerAuth")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{reviewId}")
    @Operation(summary = "Toggle like for a review")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                likeService.toggleLike(reviewId, userDetails.getUsername())
        );
    }

    @GetMapping("/{reviewId}/status")
    @Operation(summary = "Get like status and count for a review")
    public ResponseEntity<LikeResponse> getLikeStatus(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                likeService.getLikeStatus(reviewId, userDetails.getUsername())
        );
    }
}

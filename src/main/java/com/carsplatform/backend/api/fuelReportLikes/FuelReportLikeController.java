package com.carsplatform.backend.api.fuelReportLikes;

import com.carsplatform.backend.api.fuelReportLikes.dtos.FuelReportLikeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes/fuel-report")
@RequiredArgsConstructor
@Tag(name = "Fuel Report Likes", description = "API for managing fuel report likes")
@SecurityRequirement(name = "bearerAuth")
public class FuelReportLikeController {
    private final FuelReportLikeService fuelReportLikeService;

    @PostMapping("/{fuelReportId}")
    @Operation(summary = "Toggle like for a fuel report")
    public ResponseEntity<FuelReportLikeResponse> toggleLike(
            @PathVariable Long fuelReportId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                fuelReportLikeService.toggleLike(fuelReportId, userDetails.getUsername())
        );
    }

    @GetMapping("/{fuelReportId}/status")
    @Operation(summary = "Get like status and count for a fuel report")
    public ResponseEntity<FuelReportLikeResponse> getLikeStatus(
            @PathVariable Long fuelReportId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                fuelReportLikeService.getLikeStatus(fuelReportId, userDetails.getUsername())
        );
    }
}

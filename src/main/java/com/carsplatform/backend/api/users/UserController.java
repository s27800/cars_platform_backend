package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.admin.dtos.AdminFuelReportResponse;
import com.carsplatform.backend.api.admin.dtos.AdminReviewResponse;
import com.carsplatform.backend.api.dataProposal.DataProposalService;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;
import com.carsplatform.backend.api.fuelReports.FuelReportService;
import com.carsplatform.backend.api.reviews.ReviewService;
import com.carsplatform.backend.api.users.dtos.UserChangePasswordRequest;
import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.common.standard.SimpleResponse;
import com.carsplatform.backend.api.users.dtos.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "API for managing user profile")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ReviewService reviewService;
    private final FuelReportService fuelReportService;
    private final DataProposalService dataProposalService;

    @GetMapping("/me")
    @Operation(summary = "Get current user's profile information")
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        UserResponse userResponse = userMapper.toResponseDto(userService.getCurrentUser());

        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user's profile")
    public ResponseEntity<UserResponse> updateUserProfile(@Valid @RequestBody UserModifyRequest userModifyRequest) {
        userService.updateUserProfile(userModifyRequest);
        UserResponse updatedResponse = userMapper.toResponseDto(userService.getCurrentUser());

        return ResponseEntity.ok(updatedResponse);
    }

    @PostMapping("/me/change-password")
    @Operation(summary = "Change current user's password")
    public ResponseEntity<SimpleResponse> changePassword(@Valid @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changeUserPassword(userChangePasswordRequest);

        return ResponseEntity.ok(SimpleResponse.builder()
                .message("Password changed successfully.")
                .success(true)
                .build());
    }

    @GetMapping("/me/reviews")
    @Operation(summary = "Get current user's reviews")
    public ResponseEntity<Page<AdminReviewResponse>> getUserReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {

        return ResponseEntity.ok(reviewService.getReviewsForUser(userDetails.getUsername(), pageable));
    }

    @GetMapping("/me/fuel-reports")
    @Operation(summary = "Get current user's fuel reports")
    public ResponseEntity<Page<AdminFuelReportResponse>> getUserFuelReports(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {

        return ResponseEntity.ok(fuelReportService.getFuelReportsForUser(userDetails.getUsername(), pageable));
    }

    @GetMapping("/me/data-proposals")
    @Operation(summary = "Get current user's data proposals")
    public ResponseEntity<Page<GetDataProposalsResponse>> getUserDataProposals(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {

        return ResponseEntity.ok(dataProposalService.getUserDataProposals(userDetails.getUsername(), pageable));
    }
}

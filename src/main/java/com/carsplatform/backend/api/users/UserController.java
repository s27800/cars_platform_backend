package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UserChangePasswordRequest;
import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.common.standard.SimpleResponse;
import com.carsplatform.backend.api.users.dtos.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "API for managing user profile")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

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
}

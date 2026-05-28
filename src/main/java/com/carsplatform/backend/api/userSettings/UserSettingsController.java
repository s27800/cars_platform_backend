package com.carsplatform.backend.api.userSettings;

import com.carsplatform.backend.api.userSettings.dtos.UpdateUserSettingsRequest;
import com.carsplatform.backend.api.userSettings.dtos.UserSettingsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-settings")
@RequiredArgsConstructor
@Tag(name = "User Settings", description = "API for managing user settings")
@SecurityRequirement(name = "bearerAuth")
public class UserSettingsController {

    private final UserSettingsService userSettingsService;
    private final UserSettingsMapper userSettingsMapper;

    @GetMapping
    @Operation(summary = "Get current user's settings")
    public ResponseEntity<UserSettingsResponse> getCurrentUserSettings() {
        UserSettings settings = userSettingsService.getCurrentUserSettings();

        return ResponseEntity.ok(userSettingsMapper.toDto(settings));
    }

    @PutMapping
    @Operation(summary = "Update current user's settings")
    public ResponseEntity<UserSettingsResponse> updateCurrentUserSettings(
            @Valid @RequestBody UpdateUserSettingsRequest request) {

        UserSettings settings = userSettingsService.updateCurrentUserSettings(request);

        return ResponseEntity.ok(userSettingsMapper.toDto(settings));
    }
}

package com.carsplatform.backend.api.userSettings.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserSettingsRequest {

    @NotBlank(message = "Theme cannot be blank")
    @Size(max = 20, message = "Theme must be at most 20 characters")
    @Pattern(regexp = "^(light|dark)$", message = "Theme must be 'light' or 'dark'")
    private String theme;
}

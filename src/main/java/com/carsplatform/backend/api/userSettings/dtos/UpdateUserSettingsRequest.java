package com.carsplatform.backend.api.userSettings.dtos;

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

    @Size(max = 20, message = "Theme must be at most 20 characters")
    @Pattern(regexp = "^(light|dark)$", message = "Theme must be 'light' or 'dark'")
    private String theme;

    @Size(max = 10, message = "Language must be at most 10 characters")
    @Pattern(regexp = "^(en|pl)$", message = "Language must be 'en' or 'pl'")
    private String language;
}

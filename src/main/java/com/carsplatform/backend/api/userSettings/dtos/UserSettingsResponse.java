package com.carsplatform.backend.api.userSettings.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsResponse {
    private UUID id;
    private String theme;
    private String language;
}

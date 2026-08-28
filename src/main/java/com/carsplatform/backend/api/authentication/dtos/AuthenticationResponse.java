package com.carsplatform.backend.api.authentication.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private UUID userId;
    private String username;
    private Boolean isAdmin;
}

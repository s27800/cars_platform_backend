package com.carsplatform.backend.api.userSettings;

import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.userSettings.dtos.UpdateUserSettingsRequest;
import com.carsplatform.backend.common.MockMvcTestBase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("UserSettingsController Integration Tests")
class UserSettingsControllerTest extends MockMvcTestBase {

    private static final String SETTINGS_URL = "/api/user-settings";
    private static final String AUTH_URL = "/api/auth";

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {

        // Register user and get token
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("settingsuser" + System.currentTimeMillis())
                .email("settingsuser" + System.currentTimeMillis() + "@example.com")
                .password("TestPassword123!")
                .firstName("Test")
                .lastName("User")
                .build();

        String response = performPostNoAuth(AUTH_URL + "/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();
    }


    @Nested
    @DisplayName("GET /api/user-settings")
    class GetCurrentUserSettingsTests {

        @Test
        @DisplayName("returns settings when authenticated")
        void getSettings_Authenticated_Returns200() throws Exception {

            // Perform GET request with authentication and verify results -> 200 OK is returned
            performGetWithAuth(SETTINGS_URL, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.theme").value("light"))
                    .andExpect(jsonPath("$.id").exists());
        }

        @Test
        @DisplayName("returns 403 when not authenticated")
        void getSettings_NotAuthenticated_Returns403() throws Exception {

            // Perform GET request without authentication and verify response -> returns 403 Forbidden
            performGetNoAuth(SETTINGS_URL)
                    .andExpect(status().isForbidden());
        }
    }


    @Nested
    @DisplayName("PUT /api/user-settings")
    class UpdateCurrentUserSettingsTests {

        @Test
        @DisplayName("updates theme to dark successfully")
        void updateSettings_ValidRequest_Returns200() throws Exception {

            // Create request
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("dark")
                    .build();

            // Perform PUT request with authentication and verify results -> 200 OK is returned and theme is updated
            performPutWithAuth(SETTINGS_URL, request, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.theme").value("dark"));
        }

        @Test
        @DisplayName("updates theme to light successfully")
        void updateSettings_ThemeLight_Returns200() throws Exception {

            // Create request with dark theme
            UpdateUserSettingsRequest darkRequest = UpdateUserSettingsRequest.builder()
                    .theme("dark")
                    .build();

            // Perform PUT request with authentication and verify results -> 200 OK is returned and theme is updated
            performPutWithAuth(SETTINGS_URL, darkRequest, userToken)
                    .andExpect(status().isOk());

            // Create request with light theme
            UpdateUserSettingsRequest lightRequest = UpdateUserSettingsRequest.builder()
                    .theme("light")
                    .build();

            // Perform PUT request with authentication and verify results -> 200 OK is returned and theme is updated
            performPutWithAuth(SETTINGS_URL, lightRequest, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.theme").value("light"));
        }

        @Test
        @DisplayName("returns 400 for invalid theme")
        void updateSettings_InvalidTheme_Returns400() throws Exception {

            // Create invalid request
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("invalid")
                    .build();

            // Perform PUT request with authentication and verify response -> returns 400 Bad Request
            performPutWithAuth(SETTINGS_URL, request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 for blank theme")
        void updateSettings_BlankTheme_Returns400() throws Exception {

            // Create request with blank theme
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("")
                    .build();

            // Perform PUT request with authentication and verify response -> returns 400 Bad Request
            performPutWithAuth(SETTINGS_URL, request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 403 when not authenticated")
        void updateSettings_NotAuthenticated_Returns403() throws Exception {
            
            // Create request with dark theme
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("dark")
                    .build();

            // Perform PUT request without authentication and verify response -> returns 403 Forbidden
            performPutNoAuth(SETTINGS_URL, request)
                    .andExpect(status().isForbidden());
        }
    }
}

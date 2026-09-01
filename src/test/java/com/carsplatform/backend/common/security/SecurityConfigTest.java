package com.carsplatform.backend.common.security;

import com.carsplatform.backend.common.MockMvcTestBase;
import com.carsplatform.backend.common.TestSecurityUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("SecurityConfig Integration Tests")
class SecurityConfigTest extends MockMvcTestBase {


    @Nested
    @DisplayName("Public Endpoints - No Authentication Required")
    class PublicEndpointsTests {

        @Test
        @DisplayName("GET /api/brands is accessible without authentication")
        void getBrands_NoAuth_Returns200() throws Exception {
            performGetNoAuth("/api/brands")
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/cars/search is accessible without authentication")
        void searchCars_NoAuth_Returns200() throws Exception {
            performGetNoAuth("/api/cars/search")
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/body-types is accessible without authentication")
        void getBodyTypes_NoAuth_Returns200() throws Exception {
            performGetNoAuth("/api/body-types")
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/tags is accessible without authentication")
        void getTags_NoAuth_Returns200() throws Exception {
            performGetNoAuth("/api/tags")
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("POST /api/auth/register is accessible without authentication")
        void authRegister_NoAuth_NotForbidden() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .post("/api/auth/register")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}")
            ).andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("Protected Endpoints - Authentication Required")
    class ProtectedEndpointsTests {

        @Test
        @DisplayName("GET /api/users/me requires authentication")
        void getUserProfile_NoAuth_Returns401() throws Exception {
            performGetNoAuth("/api/users/me")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/users/me with valid token returns 200")
        void getUserProfile_WithAuth_Returns200() throws Exception {
            String token = registerUserAndGetToken("securitytestuser1");

            performGetWithAuth("/api/users/me", token)
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("POST /api/reviews requires authentication")
        void createReview_NoAuth_Returns401() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .post("/api/reviews/1")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}")
            ).andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/fuel-reports requires authentication")
        void createFuelReport_NoAuth_Returns401() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .post("/api/fuel-reports/1")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}")
            ).andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/likes/review/1/status requires authentication")
        void getLikeStatus_NoAuth_Returns401() throws Exception {
            performGetNoAuth("/api/likes/review/1/status")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/users/me/data-proposals requires authentication")
        void getUserDataProposals_NoAuth_Returns401() throws Exception {
            performGetNoAuth("/api/users/me/data-proposals")
                    .andExpect(status().isUnauthorized());
        }
    }


    @Nested
    @DisplayName("Admin Endpoints - Admin Role Required")
    class AdminEndpointsTests {

        @Test
        @DisplayName("GET /api/data-proposals/pending returns 403 for regular user")
        void getPendingProposals_RegularUser_Returns403() throws Exception {
            String userToken = registerUserAndGetToken("regularuser1");

            performGetWithAuth("/api/data-proposals/pending", userToken)
                    .andExpect(status().isForbidden());
        }
    }


    @Nested
    @DisplayName("Token Validation")
    class TokenValidationTests {

        @Test
        @DisplayName("invalid token format returns 401")
        void protectedEndpoint_InvalidToken_Returns401() throws Exception {
            performGetWithAuth("/api/users/me", "invalid.jwt.token")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("expired token returns 401")
        void protectedEndpoint_ExpiredToken_Returns401() throws Exception {
            String expiredToken = TestSecurityUtils.generateExpiredToken(UUID.randomUUID());

            performGetWithAuth("/api/users/me", expiredToken)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("missing Bearer prefix returns 401")
        void protectedEndpoint_NoBearerPrefix_Returns401() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .get("/api/users/me")
                            .header("Authorization", "sometoken")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
        }
    }


    private String registerUserAndGetToken(String username) throws Exception {
        com.carsplatform.backend.api.authentication.dtos.RegisterRequest request =
                com.carsplatform.backend.api.authentication.dtos.RegisterRequest.builder()
                        .username(username + System.currentTimeMillis())
                        .email(username + System.currentTimeMillis() + "@example.com")
                        .password("Password123!")
                        .firstName("Test")
                        .lastName("User")
                        .build();

        String response = performPostNoAuth("/api/auth/register", request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("accessToken").asText();
    }
}

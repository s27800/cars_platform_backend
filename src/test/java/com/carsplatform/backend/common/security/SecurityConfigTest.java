package com.carsplatform.backend.common.security;

import com.carsplatform.backend.common.MockMvcTestBase;
import com.carsplatform.backend.common.TestSecurityUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("SecurityConfig Integration Tests")
class SecurityConfigTest extends MockMvcTestBase {


    @Nested
    @DisplayName("Public Endpoints - No Authentication Required")
    class PublicEndpointsTests {

        @Test
        @DisplayName("GET /api/brands is accessible without authentication")
        void getBrands_NoAuth_Returns200() throws Exception {

            // Perform get request and verify response status is 200 OK
            performGetNoAuth("/api/brands")
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/cars/search is accessible without authentication")
        void searchCars_NoAuth_Returns200() throws Exception {

            // Perform get request and verify response status is 200 OK
            performGetNoAuth("/api/cars/search")
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/body-types is accessible without authentication")
        void getBodyTypes_NoAuth_Returns200() throws Exception {

            // Perform get request and verify response status is 200 OK
            performGetNoAuth("/api/body-types")
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /api/tags is accessible without authentication")
        void getTags_NoAuth_Returns200() throws Exception {

            // Perform get request and verify response status is 200 OK
            performGetNoAuth("/api/tags")
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("POST /api/auth/register is accessible without authentication")
        void authRegister_NoAuth_NotForbidden() throws Exception {

            // Perform post request to register user without authentication and verify response status is not 403 Forbidden
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
        void getUserProfile_NoAuth_Returns403() throws Exception {

            // Perform get request without authentication and verify response status is 403 Forbidden
            performGetNoAuth("/api/users/me")
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/users/me with valid token returns 200")
        void getUserProfile_WithAuth_Returns200() throws Exception {

            // Register user and get token
            String token = registerUserAndGetToken("securitytestuser1");

            // Perform get request with authentication and verify response status is 200 OK
            performGetWithAuth("/api/users/me", token)
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("POST /api/reviews requires authentication")
        void createReview_NoAuth_Returns403() throws Exception {

            // Perform post request without authentication and verify response status is 403 Forbidden
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .post("/api/reviews/1")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}")
            ).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/fuel-reports requires authentication")
        void createFuelReport_NoAuth_Returns403() throws Exception {

            // Perform post request without authentication and verify response status is 403 Forbidden
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .post("/api/fuel-reports/1")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}")
            ).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/likes/1/status requires authentication")
        void getLikeStatus_NoAuth_Returns403() throws Exception {

            // Perform get request without authentication and verify response status is 403 Forbidden
            performGetNoAuth("/api/likes/1/status")
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/users/me/data-proposals requires authentication")
        void getUserDataProposals_NoAuth_Returns403() throws Exception {

            // Perform get request without authentication and verify response status is 403 Forbidden
            performGetNoAuth("/api/users/me/data-proposals")
                    .andExpect(status().isForbidden());
        }
    }


    @Nested
    @DisplayName("Admin Endpoints - Admin Role Required")
    class AdminEndpointsTests {

        @Test
        @DisplayName("POST /api/brands returns 403 for regular user")
        void createBrand_RegularUser_Returns403() throws Exception {

            // Register regular user and get token
            String userToken = registerUserAndGetToken("regularuser1");

            // Perform post request with non-admin authentication and verify response status is 403 Forbidden
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .post("/api/brands")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}")
            ).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/cars/1 returns 403 for regular user")
        void deleteCar_RegularUser_Returns403() throws Exception {

            // Register regular user and get token
            String userToken = registerUserAndGetToken("regularuser2");

            // Perform delete request with non-admin authentication and verify response status is 403 Forbidden
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .delete("/api/cars/1")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            ).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("PUT /api/models/1 returns 403 for regular user")
        void updateModel_RegularUser_Returns403() throws Exception {

            // Register regular user and get token
            String userToken = registerUserAndGetToken("regularuser3");

            // Perform put request with non-admin authentication and verify response status is 403 Forbidden
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .put("/api/models/1")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content("{}")
            ).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/data-proposals/pending returns 403 for regular user")
        void getPendingProposals_RegularUser_Returns403() throws Exception {

            // Register regular user and get token
            String userToken = registerUserAndGetToken("regularuser4");

            // Perform get request with non-admin authentication and verify response status is 403 Forbidden
            performGetWithAuth("/api/data-proposals/pending", userToken)
                    .andExpect(status().isForbidden());
        }
    }


    @Nested
    @DisplayName("Token Validation")
    class TokenValidationTests {

        @Test
        @DisplayName("invalid token format returns 403")
        void protectedEndpoint_InvalidToken_Returns403() throws Exception {

            // Perform get request with invalid token and verify response status is 403 Forbidden
            performGetWithAuth("/api/users/me", "invalid.jwt.token")
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("expired token returns 403")
        void protectedEndpoint_ExpiredToken_Returns403() throws Exception {

            // Generate expired token
            String expiredToken = TestSecurityUtils.generateExpiredToken(1L);

            // Perform get request with expired token and verify response status is 403 Forbidden
            performGetWithAuth("/api/users/me", expiredToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("missing Bearer prefix returns 403")
        void protectedEndpoint_NoBearerPrefix_Returns403() throws Exception {

            // Perform get request with missing Bearer prefix and verify response status is 403 Forbidden
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .get("/api/users/me")
                            .header("Authorization", "sometoken")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            ).andExpect(status().isForbidden());
        }
    }


    // Helper method -> register user and obtain authentication token

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

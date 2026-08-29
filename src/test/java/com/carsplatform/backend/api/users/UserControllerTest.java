package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.users.dtos.UserChangePasswordRequest;
import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.common.MockMvcTestBase;
import com.carsplatform.backend.common.TestSecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("UserController Integration Tests")
class UserControllerTest extends MockMvcTestBase {

    private static final String USER_BASE_URL = "/api/users";
    private static final String AUTH_BASE_URL = "/api/auth";

    private String userToken;
    private UUID userId;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {

        // Register user and get token for authenticated tests
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testuser" + System.currentTimeMillis())
                .email("testuser" + System.currentTimeMillis() + "@example.com")
                .password("TestPassword123!")
                .firstName("Test")
                .lastName("User")
                .build();

        String response = performPostNoAuth(AUTH_BASE_URL + "/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();

        // Get user ID from token
        User user = userRepository.findByUsername(registerRequest.getUsername()).orElseThrow();
        userId = user.getId();
    }


    @Nested
    @DisplayName("GET /api/users/me")
    class GetCurrentUserProfileTests {

        @Test
        @DisplayName("returns user profile when authenticated")
        void getCurrentUserProfile_Authenticated_Returns200() throws Exception {

            // Perform request with valid token -> verify response is 200 and contains user profile data
            performGetWithAuth(USER_BASE_URL + "/me", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Test"))
                    .andExpect(jsonPath("$.lastName").value("User"))
                    .andExpect(jsonPath("$.email").exists())
                    .andExpect(jsonPath("$.password").doesNotExist());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void getCurrentUserProfile_NotAuthenticated_Returns401() throws Exception {

            // Perform request without authentication -> verify response is 403
            performGetNoAuth(USER_BASE_URL + "/me")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 401 with invalid token")
        void getCurrentUserProfile_InvalidToken_Returns401() throws Exception {

            // Perform request with invalid token -> verify response is 403
            performGetWithAuth(USER_BASE_URL + "/me", "invalid.token.here")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 401 with expired token")
        void getCurrentUserProfile_ExpiredToken_Returns401() throws Exception {

            // Generate expired token
            String expiredToken = TestSecurityUtils.generateExpiredToken(userId);

            // Perform request with expired token -> verify response is 403
            performGetWithAuth(USER_BASE_URL + "/me", expiredToken)
                    .andExpect(status().isUnauthorized());
        }
    }


    @Nested
    @DisplayName("PUT /api/users/me")
    class UpdateUserProfileTests {

        @Test
        @DisplayName("updates user profile successfully")
        void updateUserProfile_ValidData_Returns200() throws Exception {

            // Create request with updated profile data
            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName("Updated")
                    .lastName("Name")
                    .email("updated" + System.currentTimeMillis() + "@example.com")
                    .build();

            // Perform request with valid token -> verify response is 200 and contains updated profile data
            performPutWithAuth(USER_BASE_URL + "/me", request, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Updated"))
                    .andExpect(jsonPath("$.lastName").value("Name"));
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void updateUserProfile_NotAuthenticated_Returns401() throws Exception {

            // Create request with updated profile data
            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName("Updated")
                    .build();

            // Perform request without authentication -> verify response is 403
            performPutNoAuth(USER_BASE_URL + "/me", request)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 400 when email format is invalid")
        void updateUserProfile_InvalidEmail_Returns400() throws Exception {

            // Create request with invalid email format
            UserModifyRequest request = UserModifyRequest.builder()
                    .email("invalid-email")
                    .build();

            // Perform request with invalid email format -> verify response is 400
            performPutWithAuth(USER_BASE_URL + "/me", request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 409 when email already exists")
        void updateUserProfile_DuplicateEmail_Returns409() throws Exception {

            // Create another user
            String existingEmail = "existing" + System.currentTimeMillis() + "@example.com";
            RegisterRequest otherUser = RegisterRequest.builder()
                    .username("otheruser" + System.currentTimeMillis())
                    .email(existingEmail)
                    .password("Password123!")
                    .firstName("Other")
                    .lastName("User")
                    .build();

            // Perform POST request and verify response is 201 Created
            performPostNoAuth(AUTH_BASE_URL + "/register", otherUser)
                    .andExpect(status().isCreated());

            // Create request with email that already exists
            UserModifyRequest request = UserModifyRequest.builder()
                    .email(existingEmail)
                    .build();

            // Perform request with email that already exists -> verify response is 409
            performPutWithAuth(USER_BASE_URL + "/me", request, userToken)
                    .andExpect(status().isConflict());
        }
    }


    @Nested
    @DisplayName("POST /api/users/me/change-password")
    class ChangePasswordTests {

        @Test
        @DisplayName("changes password successfully with correct current password")
        void changePassword_ValidData_Returns200() throws Exception {

            // Create request with correct data
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("TestPassword123!")
                    .newPassword("NewPassword456!")
                    .build();

            // Perform request with valid token -> verify response is 200
            performPostWithAuth(USER_BASE_URL + "/me/change-password", request, userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Password changed successfully."));
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void changePassword_NotAuthenticated_Returns401() throws Exception {

            // Create request with correct data
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("OldPassword")
                    .newPassword("NewPassword123!")
                    .build();

            // Perform request without authentication -> verify response is 403
            performPostNoAuth(USER_BASE_URL + "/me/change-password", request)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 400 when current password is incorrect")
        void changePassword_WrongCurrentPassword_Returns400() throws Exception {

            // Create request with incorrect current password
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("WrongPassword123!")
                    .newPassword("NewPassword456!")
                    .build();

            // Perform request with incorrect current password -> verify response is 400
            performPostWithAuth(USER_BASE_URL + "/me/change-password", request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when new password is too short")
        void changePassword_NewPasswordTooShort_Returns400() throws Exception {

            // Create request with new invalid password
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("TestPassword123!")
                    .newPassword("short")
                    .build();

            // Perform request with new invalid password -> verify response is 400
            performPostWithAuth(USER_BASE_URL + "/me/change-password", request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when required fields are empty")
        void changePassword_EmptyFields_Returns400() throws Exception {

            // Create request with empty fields
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("")
                    .newPassword("")
                    .build();

            // Perform request with empty fields -> verify response is 400
            performPostWithAuth(USER_BASE_URL + "/me/change-password", request, userToken)
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("GET /api/users/me/data-proposals")
    class GetUserDataProposalsTests {

        @Test
        @DisplayName("returns data proposals when authenticated")
        void getUserDataProposals_Authenticated_Returns200() throws Exception {

            // Perform GET request with authentication and verify results -> 200 OK is returned
            performGetWithAuth(USER_BASE_URL + "/me/data-proposals", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void getUserDataProposals_NotAuthenticated_Returns401() throws Exception {

            // Perform GET request without authentication and verify results -> 401 Unauthorized is returned
            performGetNoAuth(USER_BASE_URL + "/me/data-proposals")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns paginated results")
        void getUserDataProposals_WithPagination_ReturnsPaginated() throws Exception {

            // Perform GET request with pagination parameters and verify results -> 200 OK is returned
            performGetWithAuth(USER_BASE_URL + "/me/data-proposals?page=0&size=5", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageable.pageSize").value(5));
        }
    }
}

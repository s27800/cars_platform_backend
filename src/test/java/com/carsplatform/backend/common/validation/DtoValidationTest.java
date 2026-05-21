package com.carsplatform.backend.common.validation;

import com.carsplatform.backend.api.authentication.dtos.LoginRequest;
import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.users.dtos.UserChangePasswordRequest;
import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.common.MockMvcTestBase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("DTO Validation Integration Tests")
class DtoValidationTest extends MockMvcTestBase {

    private String userToken;

    @BeforeEach
    void setUpUser() throws Exception {

        // Register test user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("validationuser" + System.currentTimeMillis())
                .email("validationuser" + System.currentTimeMillis() + "@example.com")
                .password("Password123!")
                .firstName("Test")
                .lastName("User")
                .build();

        // Perform registration and extract token
        String response = performPostNoAuth("/api/auth/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();
    }


    @Nested
    @DisplayName("RegisterRequest Validation")
    class RegisterRequestValidationTests {

        @Test
        @DisplayName("returns 400 when username is blank")
        void register_BlankUsername_Returns400WithFieldError() throws Exception {

            // Create request with blank username
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("valid@example.com")
                    .password("Password123!")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            // Perform request and assert validation error for username
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.username").exists());
        }

        @Test
        @DisplayName("returns 400 when email is invalid format")
        void register_InvalidEmail_Returns400WithFieldError() throws Exception {

            // Create request with invalid email
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("not-an-email")
                    .password("Password123!")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            // Perform request and assert validation error for email
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());
        }

        @Test
        @DisplayName("returns 400 when password is too short")
        void register_ShortPassword_Returns400WithFieldError() throws Exception {

            // Create request with short password
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("valid@example.com")
                    .password("123")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            // Perform request and assert validation error for password
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());
        }

        @Test
        @DisplayName("returns 400 with multiple field errors")
        void register_MultipleInvalidFields_Returns400WithMultipleErrors() throws Exception {

            // Create request with multiple invalid fields
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("invalid")
                    .password("")
                    .build();

            // Perform request and assert multiple validation errors
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isMap())
                    .andExpect(jsonPath("$.errors", aMapWithSize(greaterThanOrEqualTo(3))));
        }

        @Test
        @DisplayName("returns 400 when all fields are null")
        void register_NullFields_Returns400() throws Exception {

            // Create request with all fields null
            RegisterRequest request = RegisterRequest.builder().build();

            // Perform request and assert validation error
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }
    }


    @Nested
    @DisplayName("LoginRequest Validation")
    class LoginRequestValidationTests {

        @Test
        @DisplayName("returns 400 when username is blank")
        void login_BlankUsername_Returns400() throws Exception {

            // Create request with blank username
            LoginRequest request = LoginRequest.builder()
                    .username("")
                    .password("Password123!")
                    .build();

            // Perform request and assert validation error for username
            performPostNoAuth("/api/auth/login", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when password is blank")
        void login_BlankPassword_Returns400() throws Exception {

            // Create request with blank password
            LoginRequest request = LoginRequest.builder()
                    .username("someuser")
                    .password("")
                    .build();

            // Perform request and assert validation error for password
            performPostNoAuth("/api/auth/login", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when both fields are blank")
        void login_EmptyRequest_Returns400() throws Exception {

            // Create request with both fields blank
            LoginRequest request = LoginRequest.builder()
                    .username("")
                    .password("")
                    .build();

            // Perform request and assert validation error
            performPostNoAuth("/api/auth/login", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").exists());
        }
    }


    @Nested
    @DisplayName("UserModifyRequest Validation")
    class UserModifyRequestValidationTests {

        @Test
        @DisplayName("returns 400 when email is invalid format")
        void updateProfile_InvalidEmail_Returns400() throws Exception {

            // Create request with invalid email
            UserModifyRequest request = UserModifyRequest.builder()
                    .email("not-valid-email")
                    .build();

            // Perform request and assert validation error for email
            performPutWithAuth("/api/users/me", request, userToken)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());
        }
    }


    @Nested
    @DisplayName("UserChangePasswordRequest Validation")
    class UserChangePasswordRequestValidationTests {

        @Test
        @DisplayName("returns 400 when current password is blank")
        void changePassword_BlankCurrentPassword_Returns400() throws Exception {

            // Create request with blank current password
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("")
                    .newPassword("NewPassword123!")
                    .build();

            // Perform request and assert validation error for current password
            performPostWithAuth("/api/users/me/change-password", request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when new password is too short")
        void changePassword_ShortNewPassword_Returns400() throws Exception {

            // Create request with short new password
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("Password123!")
                    .newPassword("123")
                    .build();

            // Perform request and assert validation error for new password
            performPostWithAuth("/api/users/me/change-password", request, userToken)
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("Error Response Format")
    class ErrorResponseFormatTests {

        @Test
        @DisplayName("error response contains required fields")
        void validationError_ContainsRequiredFields() throws Exception {

            // Create request with invalid fields
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("")
                    .password("")
                    .build();

            // Perform request and assert validation error
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").isString())
                    .andExpect(jsonPath("$.timestamp").isNumber())
                    .andExpect(jsonPath("$.errors").isMap());
        }

        @Test
        @DisplayName("field errors have descriptive messages")
        void validationError_HasDescriptiveMessages() throws Exception {

            // Create request with invalid fields
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("invalid")
                    .password("123")
                    .build();

            // Perform request and assert validation error messages
            performPostNoAuth("/api/auth/register", request)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email", containsString("valid")))
                    .andExpect(jsonPath("$.errors.username").isString());
        }
    }
}

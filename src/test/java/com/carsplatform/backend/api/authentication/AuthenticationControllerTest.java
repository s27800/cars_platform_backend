package com.carsplatform.backend.api.authentication;

import com.carsplatform.backend.api.authentication.dtos.LoginRequest;
import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.common.MockMvcTestBase;
import com.carsplatform.backend.common.security.LoginAttemptService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("AuthenticationController Integration Tests")
class AuthenticationControllerTest extends MockMvcTestBase {

    private static final String AUTH_BASE_URL = "/api/auth";

    @Autowired
    private LoginAttemptService loginAttemptService;


    @BeforeEach
    @AfterEach
    void resetLoginAttempts() {
        loginAttemptService.reset();
    }


    @Nested
    @DisplayName("POST /api/auth/register")
    class RegisterTests {

        @Test
        @DisplayName("registers user successfully with valid data")
        void register_ValidData_Returns201WithToken() throws Exception {

            // Create register request with valid data
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .email("newuser@example.com")
                    .password("Password123!")
                    .firstName("John")
                    .lastName("Doe")
                    .build();

            // Perform POST request and verify response is 201 Created with access token
            performPostNoAuth(AUTH_BASE_URL + "/register", request)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.accessToken").isString());
        }

        @Test
        @DisplayName("returns 409 when username already exists")
        void register_DuplicateUsername_Returns409() throws Exception {

            // Create register request with valid data
            RegisterRequest firstRequest = RegisterRequest.builder()
                    .username("duplicateuser")
                    .email("first@example.com")
                    .password("Password123!")
                    .firstName("First")
                    .lastName("User")
                    .build();

            // Perform POST request and verify response is 201 Created
            performPostNoAuth(AUTH_BASE_URL + "/register", firstRequest)
                    .andExpect(status().isCreated());

            // Create register request with same username but different email
            RegisterRequest secondRequest = RegisterRequest.builder()
                    .username("duplicateuser")
                    .email("second@example.com")
                    .password("Password123!")
                    .firstName("Second")
                    .lastName("User")
                    .build();

            // Perform POST request and verify response is 409 Conflict
            performPostNoAuth(AUTH_BASE_URL + "/register", secondRequest)
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("returns 409 when email already exists")
        void register_DuplicateEmail_Returns409() throws Exception {

            // Create register request with valid data
            RegisterRequest firstRequest = RegisterRequest.builder()
                    .username("user1")
                    .email("duplicate@example.com")
                    .password("Password123!")
                    .firstName("First")
                    .lastName("User")
                    .build();

            // Perform POST request and verify response is 201 Created
            performPostNoAuth(AUTH_BASE_URL + "/register", firstRequest)
                    .andExpect(status().isCreated());

            // Create register request with different username but same email
            RegisterRequest secondRequest = RegisterRequest.builder()
                    .username("user2")
                    .email("duplicate@example.com")
                    .password("Password123!")
                    .firstName("Second")
                    .lastName("User")
                    .build();

            // Perform POST request and verify response is 409 Conflict
            performPostNoAuth(AUTH_BASE_URL + "/register", secondRequest)
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("returns 400 when required fields are missing")
        void register_MissingFields_Returns400() throws Exception {

            // Create register request with missing required fields
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .email("")
                    .password("")
                    .build();

            // Perform POST request and verify response is 400 Bad Request
            performPostNoAuth(AUTH_BASE_URL + "/register", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when email format is invalid")
        void register_InvalidEmailFormat_Returns400() throws Exception {

            // Create register request with invalid email format
            RegisterRequest request = RegisterRequest.builder()
                    .username("validuser")
                    .email("invalid-email")
                    .password("Password123!")
                    .firstName("John")
                    .lastName("Doe")
                    .build();

            // Perform POST request and verify response is 400 Bad Request
            performPostNoAuth(AUTH_BASE_URL + "/register", request)
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginTests {

        @Test
        @DisplayName("logs in user successfully with valid credentials")
        void login_ValidCredentials_Returns200WithToken() throws Exception {

            // Register user
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("loginuser")
                    .email("loginuser@example.com")
                    .password("Password123!")
                    .firstName("Login")
                    .lastName("User")
                    .build();

            performPostNoAuth(AUTH_BASE_URL + "/register", registerRequest)
                    .andExpect(status().isCreated());

            // Create login request with same credentials
            LoginRequest loginRequest = LoginRequest.builder()
                    .username("loginuser")
                    .password("Password123!")
                    .build();

            // Perform POST request and verify response is 200 OK with access token
            performPostNoAuth(AUTH_BASE_URL + "/login", loginRequest)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.accessToken").isString());
        }

        @Test
        @DisplayName("returns 401 when username does not exist")
        void login_NonExistingUser_Returns401() throws Exception {

            // Create login request with non-existing username
            LoginRequest request = LoginRequest.builder()
                    .username("nonexistentuser")
                    .password("Password123!")
                    .build();

            // Perform POST request and verify response is 401 Unauthorized
            performPostNoAuth(AUTH_BASE_URL + "/login", request)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 401 when password is incorrect")
        void login_WrongPassword_Returns401() throws Exception {

            // Register user
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("wrongpwduser")
                    .email("wrongpwd@example.com")
                    .password("CorrectPassword123!")
                    .firstName("Wrong")
                    .lastName("Password")
                    .build();

            performPostNoAuth(AUTH_BASE_URL + "/register", registerRequest)
                    .andExpect(status().isCreated());

            // Create login request with incorrect password
            LoginRequest loginRequest = LoginRequest.builder()
                    .username("wrongpwduser")
                    .password("WrongPassword123!")
                    .build();

            // Perform POST request and verify response is 401 Unauthorized
            performPostNoAuth(AUTH_BASE_URL + "/login", loginRequest)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 429 after too many failed login attempts")
        void login_TooManyFailedAttempts_Returns429() throws Exception {

            // Register user
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("bruteforceuser")
                    .email("bruteforce@example.com")
                    .password("CorrectPassword123!")
                    .firstName("Brute")
                    .lastName("Force")
                    .build();

            performPostNoAuth(AUTH_BASE_URL + "/register", registerRequest)
                    .andExpect(status().isCreated());

            LoginRequest wrongPassword = LoginRequest.builder()
                    .username("bruteforceuser")
                    .password("WrongPassword123!")
                    .build();

            // Exhaust the limit of failed attempts
            for (int attempt = 0; attempt < 5; attempt++)
                performPostNoAuth(AUTH_BASE_URL + "/login", wrongPassword)
                        .andExpect(status().isUnauthorized());

            // Verify the next attempt is refused before the credentials are even checked
            performPostNoAuth(AUTH_BASE_URL + "/login", wrongPassword)
                    .andExpect(status().isTooManyRequests())
                    .andExpect(header().exists("Retry-After"));

            // Verify the lock applies to the correct password as well
            LoginRequest correctPassword = LoginRequest.builder()
                    .username("bruteforceuser")
                    .password("CorrectPassword123!")
                    .build();

            performPostNoAuth(AUTH_BASE_URL + "/login", correctPassword)
                    .andExpect(status().isTooManyRequests());
        }

        @Test
        @DisplayName("returns 400 when credentials are empty")
        void login_EmptyCredentials_Returns400() throws Exception {

            // Create login request with empty credentials
            LoginRequest request = LoginRequest.builder()
                    .username("")
                    .password("")
                    .build();

            // Perform POST request and verify response is 400 Bad Request
            performPostNoAuth(AUTH_BASE_URL + "/login", request)
                    .andExpect(status().isBadRequest());
        }
    }
}

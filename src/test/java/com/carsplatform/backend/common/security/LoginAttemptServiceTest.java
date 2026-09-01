package com.carsplatform.backend.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("LoginAttemptService Unit Tests")
class LoginAttemptServiceTest {

    private static final int MAX_ATTEMPTS = 5;

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService(MAX_ATTEMPTS, 15);
    }


    @Nested
    @DisplayName("assertNotBlocked")
    class AssertNotBlockedTests {

        @Test
        @DisplayName("allows logging in when there were no failed attempts")
        void assertNotBlocked_NoFailures_DoesNotThrow() {
            assertThatCode(() -> loginAttemptService.assertNotBlocked("testuser"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("allows logging in below the limit of failed attempts")
        void assertNotBlocked_BelowLimit_DoesNotThrow() {
            for (int attempt = 0; attempt < MAX_ATTEMPTS - 1; attempt++)
                loginAttemptService.loginFailed("testuser");

            assertThatCode(() -> loginAttemptService.assertNotBlocked("testuser"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("blocks logging in after reaching the limit of failed attempts")
        void assertNotBlocked_LimitReached_Throws() {
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
                loginAttemptService.loginFailed("testuser");

            assertThatThrownBy(() -> loginAttemptService.assertNotBlocked("testuser"))
                    .isInstanceOf(TooManyLoginAttemptsException.class)
                    .hasMessageContaining("Too many failed login attempts");
        }

        @Test
        @DisplayName("does not block an account whose name differs only in letter case")
        void assertNotBlocked_DifferentCase_DoesNotThrow() {
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
                loginAttemptService.loginFailed("testuser");

            assertThatCode(() -> loginAttemptService.assertNotBlocked("TestUser"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("does not block a different account")
        void assertNotBlocked_OtherUser_DoesNotThrow() {
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
                loginAttemptService.loginFailed("testuser");

            assertThatCode(() -> loginAttemptService.assertNotBlocked("anotheruser"))
                    .doesNotThrowAnyException();
        }
    }


    @Nested
    @DisplayName("loginSucceeded")
    class LoginSucceededTests {

        @Test
        @DisplayName("clears the failure counter after a successful login")
        void loginSucceeded_ResetsCounter() {
            for (int attempt = 0; attempt < MAX_ATTEMPTS - 1; attempt++)
                loginAttemptService.loginFailed("testuser");

            loginAttemptService.loginSucceeded("testuser");

            for (int attempt = 0; attempt < MAX_ATTEMPTS - 1; attempt++)
                loginAttemptService.loginFailed("testuser");

            assertThatCode(() -> loginAttemptService.assertNotBlocked("testuser"))
                    .doesNotThrowAnyException();
        }
    }


    @Nested
    @DisplayName("reset")
    class ResetTests {

        @Test
        @DisplayName("unlocks every account")
        void reset_UnlocksAccounts() {
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
                loginAttemptService.loginFailed("testuser");

            loginAttemptService.reset();

            assertThatCode(() -> loginAttemptService.assertNotBlocked("testuser"))
                    .doesNotThrowAnyException();
        }
    }
}

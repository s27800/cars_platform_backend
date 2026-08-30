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

            // Verify a user without failed attempts is not blocked
            assertThatCode(() -> loginAttemptService.assertNotBlocked("testuser"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("allows logging in below the limit of failed attempts")
        void assertNotBlocked_BelowLimit_DoesNotThrow() {

            // Register one failed attempt less than the limit
            for (int attempt = 0; attempt < MAX_ATTEMPTS - 1; attempt++)
                loginAttemptService.loginFailed("testuser");

            // Verify the user is still allowed to try
            assertThatCode(() -> loginAttemptService.assertNotBlocked("testuser"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("blocks logging in after reaching the limit of failed attempts")
        void assertNotBlocked_LimitReached_Throws() {

            // Register failed attempts up to the limit
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
                loginAttemptService.loginFailed("testuser");

            // Verify the account is locked
            assertThatThrownBy(() -> loginAttemptService.assertNotBlocked("testuser"))
                    .isInstanceOf(TooManyLoginAttemptsException.class)
                    .hasMessageContaining("Too many failed login attempts");
        }

        @Test
        @DisplayName("does not block an account whose name differs only in letter case")
        void assertNotBlocked_DifferentCase_DoesNotThrow() {

            // Register failed attempts for a lower case username
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
                loginAttemptService.loginFailed("testuser");

            // Verify usernames are case sensitive
            assertThatCode(() -> loginAttemptService.assertNotBlocked("TestUser"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("does not block a different account")
        void assertNotBlocked_OtherUser_DoesNotThrow() {

            // Register failed attempts for one account
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
                loginAttemptService.loginFailed("testuser");

            // Verify another account is unaffected
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

            // Register failed attempts just below the limit and then a successful login
            for (int attempt = 0; attempt < MAX_ATTEMPTS - 1; attempt++)
                loginAttemptService.loginFailed("testuser");

            loginAttemptService.loginSucceeded("testuser");

            // Register the same number of failed attempts again
            for (int attempt = 0; attempt < MAX_ATTEMPTS - 1; attempt++)
                loginAttemptService.loginFailed("testuser");

            // Verify the counter started from zero so the limit has not been reached
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

            // Lock an account
            for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
                loginAttemptService.loginFailed("testuser");

            // Clear all counters
            loginAttemptService.reset();

            // Verify the account is no longer locked
            assertThatCode(() -> loginAttemptService.assertNotBlocked("testuser"))
                    .doesNotThrowAnyException();
        }
    }
}

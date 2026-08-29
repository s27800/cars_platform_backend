package com.carsplatform.backend.common.security;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Counts failed login attempts and temporarily locks an account after too many of them.
 */
@Service
@Slf4j
public class LoginAttemptService {

    private final int maxAttempts;
    private final Duration lockoutDuration;

    private final Map<String, FailedAttempts> attemptsByUsername = new ConcurrentHashMap<>();

    public LoginAttemptService(
            @Value("${app.security.login.max-attempts}") int maxAttempts,
            @Value("${app.security.login.lockout-minutes}") long lockoutMinutes
    ) {
        this.maxAttempts = maxAttempts;
        this.lockoutDuration = Duration.ofMinutes(lockoutMinutes);
    }


    /**
     * Throws error if the account is locked.
     */
    public void assertNotBlocked(String username) {
        FailedAttempts attempts = attemptsByUsername.get(key(username));
        Instant now = Instant.now();

        if (attempts == null || attempts.isExpired(now))
            return;

        if (attempts.count() >= maxAttempts)
            throw new TooManyLoginAttemptsException(Duration.between(now, attempts.expiresAt()).getSeconds());
    }


    /**
     * Records a failed login attempt for the given username.
     */
    public void loginFailed(String username) {
        Instant now = Instant.now();

        FailedAttempts attempts = attemptsByUsername.compute(key(username), (ignored, previous) -> {
            int count = (previous == null || previous.isExpired(now)) ? 1 : previous.count() + 1;
            return new FailedAttempts(count, now.plus(lockoutDuration));
        });

        if (attempts.count() == maxAttempts)
            log.warn("Account '{}' locked for {} minutes after {} failed login attempts.",
                    username, lockoutDuration.toMinutes(), attempts.count());
    }

    public void loginSucceeded(String username) {
        attemptsByUsername.remove(key(username));
    }


    // Used for tests
    public void reset() {
        attemptsByUsername.clear();
    }

    private static String key(String username) {
        return username == null ? "" : username;
    }


    /**
     * Failed attempts within one sliding window.
     */
    private record FailedAttempts(int count, Instant expiresAt) {
        boolean isExpired(Instant now) {
            return now.isAfter(expiresAt);
        }
    }
}

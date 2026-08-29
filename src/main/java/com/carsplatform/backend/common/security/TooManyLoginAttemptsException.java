package com.carsplatform.backend.common.security;

import lombok.Getter;


/**
 * Thrown when an account has been temporarily locked after too many failed login attempts.
 */
@Getter
public class TooManyLoginAttemptsException extends RuntimeException {

    private final long secondsUntilUnlock;

    public TooManyLoginAttemptsException(long secondsUntilUnlock) {
        super("Too many failed login attempts. Try again in " + Math.max(1, secondsUntilUnlock / 60) + " minute(s).");
        this.secondsUntilUnlock = secondsUntilUnlock;
    }
}

package com.carsplatform.backend.common.security.jwt;

import com.carsplatform.backend.common.security.UserPrincipal;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;


/**
 * Issues and verifies JWTs. The token carries the user id as its subject and expires after
 * {@code app.jwt.expiration-in-ms}.
 *
 * HS512 needs a key of at least 64 bytes, so a secret that is too short stops the application
 * on startup rather than quietly weakening every signature.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    /** Minimum key length required by the HMAC-SHA512 algorithm. */
    private static final int MIN_SECRET_LENGTH_IN_BYTES = 64;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-in-ms}")
    private long jwtExpirationInMs;

    private volatile SecretKey signingKey;


    @PostConstruct
    void validateConfiguration() {
        getSigningKey();
    }

    private SecretKey getSigningKey() {
        SecretKey key = signingKey;

        if (key == null) {
            key = buildSigningKey();
            signingKey = key;
        }

        return key;
    }

    private SecretKey buildSigningKey() {
        byte[] keyBytes = jwtSecret == null ? new byte[0] : jwtSecret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < MIN_SECRET_LENGTH_IN_BYTES)
            throw new IllegalStateException(
                    "app.jwt.secret must be at least " + MIN_SECRET_LENGTH_IN_BYTES
                            + " bytes long (required by HS512), but is " + keyBytes.length + " bytes.");

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
            .setSubject(userPrincipal.getId().toString())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    public UUID getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return UUID.fromString(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);

            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Rejected JWT token: {}", ex.getMessage());
        }

        return false;
    }
}

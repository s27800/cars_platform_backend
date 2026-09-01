package com.carsplatform.backend.common;

import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.security.UserPrincipal;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;


/**
 * Utility class for security-related test operations.
 */
public final class TestSecurityUtils {

    private static final String TEST_JWT_SECRET = loadJwtSecret();
    private static final int TOKEN_EXPIRATION_MS = 3600000; // 1 hour


    private TestSecurityUtils() {
    }


    private static String loadJwtSecret() {
        try (
                InputStream input = TestSecurityUtils.class.getClassLoader()
                        .getResourceAsStream("application-test.properties")
        ) {
            if (input == null)
                throw new IllegalStateException("application-test.properties not found");

            Properties props = new Properties();
            props.load(input);
            String secret = props.getProperty("app.jwt.secret");

            if (secret == null || secret.isBlank())
                throw new IllegalStateException("app.jwt.secret not found in application-test.properties");

            return secret;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load JWT secret from application-test.properties", e);
        }
    }

    public static String generateToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_EXPIRATION_MS);

        SecretKey key = Keys.hmacShaKeyFor(TEST_JWT_SECRET.getBytes());

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public static String generateToken(User user) {
        return generateToken(user.getId());
    }

    public static String generateExpiredToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 1000); // already expired

        SecretKey key = Keys.hmacShaKeyFor(TEST_JWT_SECRET.getBytes());

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date(now.getTime() - 7200000)) // issued 2 hours ago
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public static String generateTokenWithInvalidSignature(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + TOKEN_EXPIRATION_MS);

        SecretKey wrongKey = Keys.hmacShaKeyFor("WrongSecretKeyForTestingInvalidSignatureValidation12345678".getBytes());

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(wrongKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public static String bearerToken(String token) {
        return "Bearer " + token;
    }

    public static Authentication createAuthentication(User user) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);

        return new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );
    }

    public static void setAuthentication(User user) {
        Authentication authentication = createAuthentication(user);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    public static void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    public static UserPrincipal createUserPrincipal(UUID userId, String username, boolean isAdmin) {
        List<SimpleGrantedAuthority> authorities = isAdmin
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new UserPrincipal(
                userId,
                username,
                username + "@example.com",
                "encodedPassword",
                authorities
        );
    }
}

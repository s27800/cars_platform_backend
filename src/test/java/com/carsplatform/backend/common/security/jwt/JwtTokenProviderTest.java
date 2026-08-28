package com.carsplatform.backend.common.security.jwt;

import com.carsplatform.backend.common.security.UserPrincipal;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("JwtTokenProvider Unit Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String JWT_SECRET = "verySecretKeyThatIsAtLeast64BytesLongForHS512AlgorithmToWorkProperly1234567890";
    private static final int JWT_EXPIRATION_MS = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", JWT_EXPIRATION_MS);
    }


    @Nested
    @DisplayName("generateToken")
    class GenerateTokenTests {

        @Test
        @DisplayName("generates valid JWT token for authenticated user")
        void generateToken_ValidUser_ReturnsValidToken() {

            // Create authentication
            UserPrincipal userPrincipal = new UserPrincipal(
                    UUID.randomUUID(),
                    "testuser",
                    "test@example.com",
                    "password",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities()
            );

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            // Verify token is correct
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
        }

        @Test
        @DisplayName("generates token with correct user ID as subject")
        void generateToken_ValidUser_ContainsCorrectUserId() {

            // Create authentication
            UUID userId = UUID.randomUUID();

            UserPrincipal userPrincipal = new UserPrincipal(
                    userId,
                    "testuser",
                    "test@example.com",
                    "password",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities()
            );

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            // Verify token contains correct user ID
            UUID extractedUserId = jwtTokenProvider.getUserIdFromJWT(token);

            assertThat(extractedUserId).isEqualTo(userId);
        }
    }


    @Nested
    @DisplayName("getUserIdFromJWT")
    class GetUserIdFromJWTTests {

        @Test
        @DisplayName("extracts user ID from valid token")
        void getUserIdFromJWT_ValidToken_ReturnsUserId() {

            // Generate authentication and token
            UUID expectedUserId = UUID.randomUUID();
            UserPrincipal userPrincipal = new UserPrincipal(
                    expectedUserId,
                    "testuser",
                    "test@example.com",
                    "password",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities()
            );

            String token = jwtTokenProvider.generateToken(authentication);

            // Verify user ID can be extracted from token
            UUID userId = jwtTokenProvider.getUserIdFromJWT(token);

            assertThat(userId).isEqualTo(expectedUserId);
        }
    }


    @Nested
    @DisplayName("validateToken")
    class ValidateTokenTests {

        @Test
        @DisplayName("returns true for valid token")
        void validateToken_ValidToken_ReturnsTrue() {

            // Generate authentication and token
            UserPrincipal userPrincipal = new UserPrincipal(
                    UUID.randomUUID(),
                    "testuser",
                    "test@example.com",
                    "password",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities()
            );
            String token = jwtTokenProvider.generateToken(authentication);

            // Verify token is valid
            boolean isValid = jwtTokenProvider.validateToken(token);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("returns false for expired token")
        void validateToken_ExpiredToken_ReturnsFalse() {

            // Generate expired token
            SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

            String expiredToken = Jwts.builder()
                    .setSubject("1")
                    .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                    .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();

            // Verify token is invalid due to expiration
            boolean isValid = jwtTokenProvider.validateToken(expiredToken);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("returns false for malformed token")
        void validateToken_MalformedToken_ReturnsFalse() {

            // Generate malformed token
            String malformedToken = "not.a.valid.jwt.token";

            // Verify token is invalid due to being malformed
            boolean isValid = jwtTokenProvider.validateToken(malformedToken);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("returns false for token with invalid signature")
        void validateToken_InvalidSignature_ReturnsFalse() {

            // Generate token with different secret key to simulate invalid signature
            String differentSecret = "aDifferentSecretKeyThatIsAlsoAtLeast64BytesLongForTheHS512Algorithm12345678";
            SecretKey differentKey = Keys.hmacShaKeyFor(differentSecret.getBytes());

            String tokenWithDifferentKey = Jwts.builder()
                    .setSubject("1")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                    .signWith(differentKey, SignatureAlgorithm.HS512)
                    .compact();

            // Verify token is invalid due to signature not matching the expected secret
            boolean isValid = jwtTokenProvider.validateToken(tokenWithDifferentKey);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("returns false for empty token")
        void validateToken_EmptyToken_ReturnsFalse() {

            // Validate empty token
            boolean isValid = jwtTokenProvider.validateToken("");

            // Verify token is invalid due to being empty
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("returns false for null token")
        void validateToken_NullToken_ReturnsFalse() {

            // Validate null token
            boolean isValid = jwtTokenProvider.validateToken(null);

            // Verify token is invalid due to being null
            assertThat(isValid).isFalse();
        }
    }
}

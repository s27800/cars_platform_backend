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

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DisplayName("JwtTokenProvider Unit Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String JWT_SECRET = "verySecretKeyThatIsAtLeast64BytesLongForHS512AlgorithmToWorkProperly1234567890";
    private static final long JWT_EXPIRATION_MS = 3600000L; // 1 hour

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

            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
        }

        @Test
        @DisplayName("generates token with correct user ID as subject")
        void generateToken_ValidUser_ContainsCorrectUserId() {
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

            String token = jwtTokenProvider.generateToken(authentication);

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

            boolean isValid = jwtTokenProvider.validateToken(token);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("returns false for expired token")
        void validateToken_ExpiredToken_ReturnsFalse() {
            SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

            String expiredToken = Jwts.builder()
                    .setSubject("1")
                    .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                    .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();

            boolean isValid = jwtTokenProvider.validateToken(expiredToken);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("returns false for malformed token")
        void validateToken_MalformedToken_ReturnsFalse() {
            String malformedToken = "not.a.valid.jwt.token";

            boolean isValid = jwtTokenProvider.validateToken(malformedToken);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("returns false for token with invalid signature")
        void validateToken_InvalidSignature_ReturnsFalse() {
            String differentSecret = "aDifferentSecretKeyThatIsAlsoAtLeast64BytesLongForTheHS512Algorithm12345678";
            SecretKey differentKey = Keys.hmacShaKeyFor(differentSecret.getBytes());

            String tokenWithDifferentKey = Jwts.builder()
                    .setSubject("1")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                    .signWith(differentKey, SignatureAlgorithm.HS512)
                    .compact();

            boolean isValid = jwtTokenProvider.validateToken(tokenWithDifferentKey);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("returns false for empty token")
        void validateToken_EmptyToken_ReturnsFalse() {
            boolean isValid = jwtTokenProvider.validateToken("");

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("returns false for null token")
        void validateToken_NullToken_ReturnsFalse() {
            boolean isValid = jwtTokenProvider.validateToken(null);

            assertThat(isValid).isFalse();
        }
    }


    @Nested
    @DisplayName("configuration")
    class ConfigurationTests {

        @Test
        @DisplayName("rejects a secret shorter than the 64 bytes required by HS512")
        void signingKey_TooShortSecret_ThrowsException() {
            JwtTokenProvider providerWithShortSecret = new JwtTokenProvider();

            ReflectionTestUtils.setField(providerWithShortSecret, "jwtSecret", "tooShortSecret");
            ReflectionTestUtils.setField(providerWithShortSecret, "jwtExpirationInMs", JWT_EXPIRATION_MS);

            assertThatThrownBy(providerWithShortSecret::validateConfiguration)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("at least 64 bytes");
        }

        @Test
        @DisplayName("signs the secret as UTF-8 regardless of the platform encoding")
        void signingKey_NonAsciiSecret_UsesUtf8() {
            String nonAsciiSecret = "zażółćGęśląJaźńSekretnyKluczDoPodpisywaniaTokenówJWTwAplikacjiCars";

            JwtTokenProvider providerWithNonAsciiSecret = new JwtTokenProvider();

            ReflectionTestUtils.setField(providerWithNonAsciiSecret, "jwtSecret", nonAsciiSecret);
            ReflectionTestUtils.setField(providerWithNonAsciiSecret, "jwtExpirationInMs", JWT_EXPIRATION_MS);

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

            String token = providerWithNonAsciiSecret.generateToken(authentication);

            SecretKey utf8Key = Keys.hmacShaKeyFor(nonAsciiSecret.getBytes(StandardCharsets.UTF_8));

            assertThatCode(() -> Jwts.parserBuilder().setSigningKey(utf8Key).build().parseClaimsJws(token))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("handles an expiration longer than Integer.MAX_VALUE milliseconds")
        void generateToken_ExpirationBeyondIntRange_ReturnsFutureExpiry() {
            long thirtyDaysInMs = 30L * 24 * 60 * 60 * 1000;

            JwtTokenProvider providerWithLongExpiration = new JwtTokenProvider();

            ReflectionTestUtils.setField(providerWithLongExpiration, "jwtSecret", JWT_SECRET);
            ReflectionTestUtils.setField(providerWithLongExpiration, "jwtExpirationInMs", thirtyDaysInMs);

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

            String token = providerWithLongExpiration.generateToken(authentication);

            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            assertThat(expiration).isAfter(new Date());
        }
    }
}

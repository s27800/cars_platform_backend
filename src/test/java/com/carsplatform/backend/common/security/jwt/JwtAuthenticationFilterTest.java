package com.carsplatform.backend.common.security.jwt;

import com.carsplatform.backend.api.users.UserService;
import com.carsplatform.backend.common.security.UserPrincipal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Unit Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }


    @Nested
    @DisplayName("doFilterInternal")
    class DoFilterInternalTests {

        @Test
        @DisplayName("sets authentication when valid token provided")
        void doFilter_ValidToken_SetsAuthentication() throws Exception {
            String validToken = "valid.jwt.token";
            UUID userId = UUID.randomUUID();

            UserPrincipal userPrincipal = new UserPrincipal(
                    userId,
                    "testuser",
                    "test@example.com",
                    "password",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
            when(tokenProvider.validateToken(validToken)).thenReturn(true);
            when(tokenProvider.getUserIdFromJWT(validToken)).thenReturn(userId);
            when(userService.loadUserById(userId)).thenReturn(userPrincipal);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .isEqualTo(userPrincipal);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("continues filter chain without authentication when no token")
        void doFilter_NoToken_ContinuesWithoutAuth() throws Exception {
            when(request.getHeader("Authorization")).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(tokenProvider, never()).validateToken(anyString());
        }

        @Test
        @DisplayName("returns 401 Unauthorized when invalid token")
        void doFilter_InvalidToken_Returns401() throws Exception {
            String invalidToken = "invalid.jwt.token";
            java.io.PrintWriter writer = mock(java.io.PrintWriter.class);

            when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
            when(tokenProvider.validateToken(invalidToken)).thenReturn(false);
            when(response.getWriter()).thenReturn(writer);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).setContentType("application/json");
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("returns 401 Unauthorized when token validation throws exception")
        void doFilter_ExceptionDuringValidation_Returns401() throws Exception {
            String token = "some.jwt.token";
            java.io.PrintWriter writer = mock(java.io.PrintWriter.class);

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(tokenProvider.validateToken(token)).thenThrow(new RuntimeException("Validation error"));
            when(response.getWriter()).thenReturn(writer);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            verify(response).setContentType("application/json");
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("ignores authorization header without Bearer prefix")
        void doFilter_NoBearerPrefix_ContinuesWithoutAuth() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(tokenProvider, never()).validateToken(anyString());
        }

        @Test
        @DisplayName("handles empty Bearer token")
        void doFilter_EmptyBearerToken_ContinuesWithoutAuth() throws Exception {
            when(request.getHeader("Authorization")).thenReturn("Bearer ");

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("sets correct authorities from user principal")
        void doFilter_ValidAdminToken_SetsAdminAuthorities() throws Exception {
            String validToken = "admin.jwt.token";
            UUID userId = UUID.randomUUID();

            UserPrincipal adminPrincipal = new UserPrincipal(
                    userId,
                    "admin",
                    "admin@example.com",
                    "password",
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
            when(tokenProvider.validateToken(validToken)).thenReturn(true);
            when(tokenProvider.getUserIdFromJWT(validToken)).thenReturn(userId);
            when(userService.loadUserById(userId)).thenReturn(adminPrincipal);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                    .extracting("authority")
                    .contains("ROLE_ADMIN");
        }
    }
}

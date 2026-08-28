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

import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
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

            // Create valid token and user principal
            String validToken = "valid.jwt.token";
            UUID userId = UUID.randomUUID();

            UserPrincipal userPrincipal = new UserPrincipal(
                    userId,
                    "testuser",
                    "test@example.com",
                    "password",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // Mock dependencies
            when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
            when(tokenProvider.validateToken(validToken)).thenReturn(true);
            when(tokenProvider.getUserIdFromJWT(validToken)).thenReturn(userId);
            when(userService.loadUserById(userId)).thenReturn(userPrincipal);

            // Execute filter
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Verify authentication is set in security context
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
            assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .isEqualTo(userPrincipal);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("continues filter chain without authentication when no token")
        void doFilter_NoToken_ContinuesWithoutAuth() throws Exception {

            // Mock dependencies
            when(request.getHeader("Authorization")).thenReturn(null);

            // Execute filter
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Verify no authentication is set and filter chain continues
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(tokenProvider, never()).validateToken(anyString());
        }

        @Test
        @DisplayName("returns 403 Forbidden when invalid token")
        void doFilter_InvalidToken_Returns403() throws Exception {

            // Mock dependencies
            String invalidToken = "invalid.jwt.token";
            java.io.PrintWriter writer = mock(java.io.PrintWriter.class);

            when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
            when(tokenProvider.validateToken(invalidToken)).thenReturn(false);
            when(response.getWriter()).thenReturn(writer);

            // Execute filter
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Verify 403 is returned and filter chain does NOT continue
            verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            verify(response).setContentType("application/json");
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("returns 403 Forbidden when token validation throws exception")
        void doFilter_ExceptionDuringValidation_Returns403() throws Exception {

            // Mock dependencies
            String token = "some.jwt.token";
            java.io.PrintWriter writer = mock(java.io.PrintWriter.class);

            when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
            when(tokenProvider.validateToken(token)).thenThrow(new RuntimeException("Validation error"));
            when(response.getWriter()).thenReturn(writer);

            // Execute filter
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Verify 403 is returned and filter chain does NOT continue
            verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            verify(response).setContentType("application/json");
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("ignores authorization header without Bearer prefix")
        void doFilter_NoBearerPrefix_ContinuesWithoutAuth() throws Exception {

            // Mock dependencies
            when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

            // Execute filter
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Verify no authentication is set and filter chain continues
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
            verify(tokenProvider, never()).validateToken(anyString());
        }

        @Test
        @DisplayName("handles empty Bearer token")
        void doFilter_EmptyBearerToken_ContinuesWithoutAuth() throws Exception {

            // Mock dependencies
            when(request.getHeader("Authorization")).thenReturn("Bearer ");

            // Execute filter
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Verify no authentication is set and filter chain continues
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("sets correct authorities from user principal")
        void doFilter_ValidAdminToken_SetsAdminAuthorities() throws Exception {

            // Create valid token and admin user principal
            String validToken = "admin.jwt.token";
            UUID userId = UUID.randomUUID();

            UserPrincipal adminPrincipal = new UserPrincipal(
                    userId,
                    "admin",
                    "admin@example.com",
                    "password",
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );

            // Mock dependencies
            when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
            when(tokenProvider.validateToken(validToken)).thenReturn(true);
            when(tokenProvider.getUserIdFromJWT(validToken)).thenReturn(userId);
            when(userService.loadUserById(userId)).thenReturn(adminPrincipal);

            // Execute filter
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // Verify authentication is set with correct authorities
            assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                    .extracting("authority")
                    .contains("ROLE_ADMIN");
        }
    }
}

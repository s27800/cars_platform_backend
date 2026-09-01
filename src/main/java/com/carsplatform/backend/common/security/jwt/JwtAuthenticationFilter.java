package com.carsplatform.backend.common.security.jwt;

import com.carsplatform.backend.api.users.UserService;
import com.carsplatform.backend.common.security.SecurityErrorResponseWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;


/**
 * Reads the {@code Authorization: Bearer <token>} header and puts the user it points to into
 * the security context.
 *
 * A request without the header passes through unauthenticated so that public endpoints keep
 * working. A token that is present but broken or expired ends the request with 401 instead of
 * being ignored, so a client whose session has expired learns about it on the first request
 * rather than on the first protected one.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                if (tokenProvider.validateToken(jwt)) {
                    UUID userId = tokenProvider.getUserIdFromJWT(jwt);

                    UserDetails userDetails = userService.loadUserById(userId);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid or expired authentication token.");

                    return;
                }
            }
        } catch (Exception ex) {
            log.warn("Could not set user authentication in security context: {}", ex.getMessage());

            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid or expired authentication token.");

            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
            return bearerToken.substring(7);

        return null;
    }
}

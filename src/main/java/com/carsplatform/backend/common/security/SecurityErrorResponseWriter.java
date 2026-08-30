package com.carsplatform.backend.common.security;

import com.carsplatform.backend.common.standard.ErrorResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * Writes error responses produced inside the security filter chain.
 */
public final class SecurityErrorResponseWriter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private SecurityErrorResponseWriter() {
    }

    public static void write(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}

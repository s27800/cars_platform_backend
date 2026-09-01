package com.carsplatform.backend.common.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;


@DisplayName("SecurityErrorResponseWriter Tests")
class SecurityErrorResponseWriterTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private MockHttpServletResponse response;


    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
    }

    private JsonNode writtenBody() throws IOException {
        return objectMapper.readTree(response.getContentAsString());
    }


    @Nested
    @DisplayName("Response metadata")
    class ResponseMetadataTests {

        @Test
        @DisplayName("Should set the status it was given")
        void shouldSetStatus() throws IOException {
            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        }

        @Test
        @DisplayName("Should declare a JSON content type")
        void shouldSetJsonContentType() throws IOException {
            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden");

            assertThat(response.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);
        }

        @Test
        @DisplayName("Should declare UTF-8 so a translated message survives the trip")
        void shouldSetUtf8Encoding() throws IOException {
            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_FORBIDDEN, "Brak uprawnień");

            assertThat(response.getCharacterEncoding()).isEqualToIgnoringCase(StandardCharsets.UTF_8.name());
        }
    }


    @Nested
    @DisplayName("Response body")
    class ResponseBodyTests {

        @Test
        @DisplayName("Should write the status into the body")
        void shouldWriteStatus() throws IOException {
            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

            assertThat(writtenBody().get("status").asInt()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        }

        @Test
        @DisplayName("Should write the message into the body")
        void shouldWriteMessage() throws IOException {
            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");

            assertThat(writtenBody().get("message").asText()).isEqualTo("Token expired");
        }

        @Test
        @DisplayName("Should stamp the body with the current time")
        void shouldWriteTimestamp() throws IOException {
            long before = System.currentTimeMillis();

            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

            long timestamp = writtenBody().get("timestamp").asLong();

            assertThat(timestamp).isBetween(before, System.currentTimeMillis());
        }

        @Test
        @DisplayName("Should match the shape the API uses for every other error")
        void shouldMatchTheStandardErrorShape() throws IOException {
            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden");

            JsonNode body = writtenBody();

            assertThat(body.has("status")).isTrue();
            assertThat(body.has("message")).isTrue();
            assertThat(body.has("timestamp")).isTrue();
        }

        @Test
        @DisplayName("Should accept a null message")
        void shouldAcceptNullMessage() throws IOException {
            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED, null);

            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
            assertThat(writtenBody().get("status").asInt()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        }

        @Test
        @DisplayName("Should escape a message that would otherwise break the JSON")
        void shouldEscapeMessage() throws IOException {
            SecurityErrorResponseWriter.write(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Bad \"token\": {\"role\":\"ADMIN\"}");

            assertThat(writtenBody().get("message").asText()).isEqualTo("Bad \"token\": {\"role\":\"ADMIN\"}");
        }
    }
}

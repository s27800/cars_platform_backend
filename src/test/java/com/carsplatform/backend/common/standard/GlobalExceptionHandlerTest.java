package com.carsplatform.backend.common.standard;

import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }


    @Nested
    @DisplayName("handleResourceNotFoundException")
    class ResourceNotFoundExceptionTests {

        @Test
        @DisplayName("returns 404 with error message")
        void handleResourceNotFoundException_Returns404WithDetails() {
            ResourceNotFoundException ex = new ResourceNotFoundException("Car", "id", 123);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(404);
            assertThat(response.getBody().getMessage()).contains("Car");
            assertThat(response.getBody().getMessage()).contains("123");
            assertThat(response.getBody().getTimestamp()).isPositive();
        }

        @Test
        @DisplayName("preserves original exception message")
        void handleResourceNotFoundException_PreservesMessage() {
            ResourceNotFoundException ex = new ResourceNotFoundException("User", "username", "john");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

            assertThat(response.getBody().getMessage()).contains("User");
            assertThat(response.getBody().getMessage()).contains("john");
        }
    }


    @Nested
    @DisplayName("handleResourceAlreadyExistsException")
    class ResourceAlreadyExistsExceptionTests {

        @Test
        @DisplayName("returns 409 with conflict message")
        void handleResourceAlreadyExistsException_Returns409() {
            ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("email", "test@example.com");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceAlreadyExistsException(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
            assertThat(response.getBody().getMessage()).contains("already exists");
        }
    }


    @Nested
    @DisplayName("handleMethodArgumentNotValid")
    class MethodArgumentNotValidTests {

        @Test
        @DisplayName("returns 400 with field errors")
        void handleMethodArgumentNotValid_Returns400WithFieldErrors() {
            FieldError fieldError1 = new FieldError("request", "email", "Email must be valid");
            FieldError fieldError2 = new FieldError("request", "password", "Password is required");

            when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(
                    methodArgumentNotValidException, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).contains("Validation failed");
            assertThat(response.getBody().getErrors()).isNotNull();
            assertThat(response.getBody().getErrors()).containsKey("email");
            assertThat(response.getBody().getErrors()).containsKey("password");
            assertThat(response.getBody().getErrors().get("email")).isEqualTo("Email must be valid");
            assertThat(response.getBody().getErrors().get("password")).isEqualTo("Password is required");
        }

        @Test
        @DisplayName("returns 400 with single field error")
        void handleMethodArgumentNotValid_SingleFieldError_Returns400() {
            FieldError fieldError = new FieldError("request", "username", "Username cannot be blank");

            when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValid(
                    methodArgumentNotValidException, webRequest);

            assertThat(response.getBody().getErrors()).hasSize(1);
            assertThat(response.getBody().getErrors().get("username")).isEqualTo("Username cannot be blank");
        }
    }


    @Nested
    @DisplayName("handleAccessDeniedException")
    class AccessDeniedExceptionTests {

        @Test
        @DisplayName("returns 403 with permission message")
        void handleAccessDeniedException_Returns403() {
            AccessDeniedException ex = new AccessDeniedException("Access denied");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(403);
            assertThat(response.getBody().getMessage()).contains("permission");
        }
    }


    @Nested
    @DisplayName("handleAuthenticationException")
    class AuthenticationExceptionTests {

        @Test
        @DisplayName("returns 401 for BadCredentialsException")
        void handleBadCredentialsException_Returns401() {
            BadCredentialsException ex = new BadCredentialsException("Invalid credentials");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthenticationException(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(401);
            assertThat(response.getBody().getMessage()).contains("Invalid");
        }
    }


    @Nested
    @DisplayName("handleIllegalArgumentException")
    class IllegalArgumentExceptionTests {

        @Test
        @DisplayName("returns 400 with error message")
        void handleIllegalArgumentException_Returns400() {
            String message = "Current password is incorrect.";
            IllegalArgumentException ex = new IllegalArgumentException(message);

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo(message);
        }
    }


    @Nested
    @DisplayName("handleGlobalException")
    class GlobalExceptionTests {

        @Test
        @DisplayName("returns 500 for unexpected exceptions")
        void handleGlobalException_Returns500() {
            Exception ex = new RuntimeException("Unexpected error");

            ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(500);
            assertThat(response.getBody().getMessage()).contains("internal server error");
        }
    }
}

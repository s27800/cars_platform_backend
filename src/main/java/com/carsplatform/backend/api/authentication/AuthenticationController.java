package com.carsplatform.backend.api.authentication;

import com.carsplatform.backend.api.authentication.dtos.LoginRequest;
import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.authentication.dtos.AuthenticationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/login")
    @Operation(summary = "Authenticate existing user")
    public ResponseEntity<AuthenticationResponse> loginUser(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authenticationService.loginUser(loginRequest));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<AuthenticationResponse> registerUser(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return new ResponseEntity<>(authenticationService.registerUser(registerRequest), HttpStatus.CREATED);
    }
}

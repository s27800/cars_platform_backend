package com.carsplatform.backend.api.authentication;

import com.carsplatform.backend.api.authentication.dtos.AuthenticationResponse;
import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.authentication.dtos.LoginRequest;
import com.carsplatform.backend.api.userSettings.UserSettings;
import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.security.jwt.JwtTokenProvider;
import com.carsplatform.backend.common.security.LoginAttemptService;
import com.carsplatform.backend.common.security.UserPrincipal;
import com.carsplatform.backend.common.security.crypto.BlindIndexService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Registration and login.
 *
 * Passwords are hashed with BCrypt before they are saved. Failed logins are counted by
 * {@link LoginAttemptService}, which blocks the account for a while once there have been too
 * many failed attempts, so the endpoint cannot be used to guess passwords.
 *
 * The address, first name and last name given at registration are encrypted before they
 * reach the database; the address is checked for duplicates through its blind index, because
 * the ciphertext of the same address is different every time.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final LoginAttemptService loginAttemptService;
    private final BlindIndexService blindIndexService;


    public AuthenticationResponse loginUser(LoginRequest loginRequest) {
        loginAttemptService.assertNotBlocked(loginRequest.getUsername());

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            loginAttemptService.loginFailed(loginRequest.getUsername());

            throw ex;
        }

        loginAttemptService.loginSucceeded(loginRequest.getUsername());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .userId(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .isAdmin(userPrincipal.isAdmin())
                .build();
    }

    public AuthenticationResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new ResourceAlreadyExistsException("Username", registerRequest.getUsername());

        if (userRepository.existsByEmailHash(blindIndexService.hash(registerRequest.getEmail())))
            throw new ResourceAlreadyExistsException("Email", registerRequest.getEmail());

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .isAdmin(false)
                .build();

        UserSettings settings = UserSettings.builder()
                .theme("light")
                .user(user)
                .build();

        user.setUserSettings(settings);
        userRepository.save(user);

        return loginUser(new LoginRequest(registerRequest.getUsername(), registerRequest.getPassword()));
    }
}

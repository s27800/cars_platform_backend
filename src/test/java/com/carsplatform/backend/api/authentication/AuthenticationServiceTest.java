package com.carsplatform.backend.api.authentication;

import com.carsplatform.backend.api.authentication.dtos.AuthenticationResponse;
import com.carsplatform.backend.api.authentication.dtos.LoginRequest;
import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;
import com.carsplatform.backend.common.security.UserPrincipal;
import com.carsplatform.backend.common.security.jwt.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(1L)
                .build();
    }


    @Nested
    @DisplayName("loginUser")
    class LoginUserTests {

        @Test
        @DisplayName("should return AuthenticationResponse with token when credentials are valid")
        void login_ValidCredentials_ReturnsJwtToken() {

            // Create valid login request
            LoginRequest loginRequest = new LoginRequest("testuser", "password123");

            UserPrincipal userPrincipal = UserPrincipal.create(testUser);
            Authentication authentication = mock(Authentication.class);

            // Mock authentication manager and token provider behavior
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);
            when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token-123");

            // Login user
            AuthenticationResponse response = authenticationService.loginUser(loginRequest);

            // Verify results -> response is valid
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("jwt-token-123");
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getIsAdmin()).isFalse();

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenProvider).generateToken(authentication);
        }

        @Test
        @DisplayName("should throw BadCredentialsException when credentials are invalid")
        void login_InvalidCredentials_ThrowsException() {

            // Create invalid login request
            LoginRequest loginRequest = new LoginRequest("testuser", "wrongPassword");

            // Mock authentication manager behavior
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            // Login user and verify results -> BadCredentialsException is thrown
            assertThatThrownBy(() -> authenticationService.loginUser(loginRequest))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("should return isAdmin=true for admin user")
        void login_AdminUser_ReturnsIsAdminTrue() {

            // Create admin user and valid login request
            User adminUser = TestDataFactory.adminUser()
                    .id(2L)
                    .build();

            LoginRequest loginRequest = new LoginRequest("admin", "password123");

            UserPrincipal adminPrincipal = UserPrincipal.create(adminUser);
            Authentication authentication = mock(Authentication.class);

            // Mock authentication manager and token provider behavior
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminPrincipal);
            when(tokenProvider.generateToken(authentication)).thenReturn("admin-jwt-token");

            // Login admin user
            AuthenticationResponse response = authenticationService.loginUser(loginRequest);

            // Verify results -> response is valid
            assertThat(response.getIsAdmin()).isTrue();
            assertThat(response.getUsername()).isEqualTo("admin");
        }
    }


    @Nested
    @DisplayName("registerUser")
    class RegisterUserTests {

        @Test
        @DisplayName("should create user and return token when data is valid")
        void register_ValidData_CreatesUserAndReturnsToken() {

            // Create valid register request
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("newuser")
                    .email("newuser@example.com")
                    .password("password123")
                    .firstName("New")
                    .lastName("User")
                    .build();

            // Mock user repository behavior
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

            // Create user
            User savedUser = User.builder()
                    .id(1L)
                    .username("newuser")
                    .email("newuser@example.com")
                    .password("encodedPassword")
                    .firstName("New")
                    .lastName("User")
                    .isAdmin(false)
                    .build();

            UserPrincipal userPrincipal = UserPrincipal.create(savedUser);
            Authentication authentication = mock(Authentication.class);

            // Mock authentication manager and token provider behavior
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);
            when(tokenProvider.generateToken(authentication)).thenReturn("new-user-jwt-token");

            // Register user
            AuthenticationResponse response = authenticationService.registerUser(registerRequest);

            // Verify results -> response is valid
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("new-user-jwt-token");

            // Verify user was saved
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            // Verify captured user has correct data
            User capturedUser = userCaptor.getValue();

            assertThat(capturedUser.getUsername()).isEqualTo("newuser");
            assertThat(capturedUser.getEmail()).isEqualTo("newuser@example.com");
            assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
            assertThat(capturedUser.getIsAdmin()).isFalse();
            assertThat(capturedUser.getUserSettings()).isNotNull();
            assertThat(capturedUser.getUserSettings().getTheme()).isEqualTo("light");
        }

        @Test
        @DisplayName("should throw ResourceAlreadyExistsException when username exists")
        void register_DuplicateUsername_ThrowsException() {

            // Create register request
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("existinguser")
                    .email("new@example.com")
                    .password("password123")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            // Mock user repository behavior
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            // Register user and verify results -> ResourceAlreadyExistsException is thrown
            assertThatThrownBy(() -> authenticationService.registerUser(registerRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceAlreadyExistsException when email exists")
        void register_DuplicateEmail_ThrowsException() {

            // Create register request
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("newuser")
                    .email("existing@example.com")
                    .password("password123")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            // Mock user repository behavior
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            // Register user and verify results -> ResourceAlreadyExistsException is thrown
            assertThatThrownBy(() -> authenticationService.registerUser(registerRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class);

            verify(userRepository, never()).save(any());
        }
    }
}

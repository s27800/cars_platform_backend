package com.carsplatform.backend.api.authentication;

import com.carsplatform.backend.api.authentication.dtos.AuthenticationResponse;
import com.carsplatform.backend.api.authentication.dtos.LoginRequest;
import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;
import com.carsplatform.backend.common.security.LoginAttemptService;
import com.carsplatform.backend.common.security.TooManyLoginAttemptsException;
import com.carsplatform.backend.common.security.UserPrincipal;
import com.carsplatform.backend.common.security.jwt.JwtTokenProvider;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Nested
    @DisplayName("loginUser")
    class LoginUserTests {

        @Test
        @DisplayName("should return AuthenticationResponse with token when credentials are valid")
        void login_ValidCredentials_ReturnsJwtToken() {
            LoginRequest loginRequest = new LoginRequest("testuser", "password123");

            UserPrincipal userPrincipal = UserPrincipal.create(testUser);
            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);
            when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token-123");

            AuthenticationResponse response = authenticationService.loginUser(loginRequest);
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("jwt-token-123");
            assertThat(response.getUserId()).isEqualTo(testUser.getId());
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getIsAdmin()).isFalse();

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(tokenProvider).generateToken(authentication);

            verify(loginAttemptService).loginSucceeded("testuser");
        }

        @Test
        @DisplayName("should throw BadCredentialsException when credentials are invalid")
        void login_InvalidCredentials_ThrowsException() {
            LoginRequest loginRequest = new LoginRequest("testuser", "wrongPassword");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authenticationService.loginUser(loginRequest))
                    .isInstanceOf(BadCredentialsException.class);

            verify(loginAttemptService).loginFailed("testuser");
        }

        @Test
        @DisplayName("should not authenticate at all when the account is temporarily locked")
        void login_BlockedAccount_ThrowsWithoutAuthenticating() {
            LoginRequest loginRequest = new LoginRequest("testuser", "password123");

            doThrow(new TooManyLoginAttemptsException(900))
                    .when(loginAttemptService).assertNotBlocked("testuser");

            assertThatThrownBy(() -> authenticationService.loginUser(loginRequest))
                    .isInstanceOf(TooManyLoginAttemptsException.class);

            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("should return isAdmin=true for admin user")
        void login_AdminUser_ReturnsIsAdminTrue() {
            User adminUser = TestDataFactory.adminUser()
                    .id(UUID.randomUUID())
                    .build();

            LoginRequest loginRequest = new LoginRequest("admin", "password123");

            UserPrincipal adminPrincipal = UserPrincipal.create(adminUser);
            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(adminPrincipal);
            when(tokenProvider.generateToken(authentication)).thenReturn("admin-jwt-token");

            AuthenticationResponse response = authenticationService.loginUser(loginRequest);
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
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("newuser")
                    .email("newuser@example.com")
                    .password("password123")
                    .firstName("New")
                    .lastName("User")
                    .build();

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

            User savedUser = User.builder()
                    .id(UUID.randomUUID())
                    .username("newuser")
                    .email("newuser@example.com")
                    .password("encodedPassword")
                    .firstName("New")
                    .lastName("User")
                    .isAdmin(false)
                    .build();

            UserPrincipal userPrincipal = UserPrincipal.create(savedUser);
            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userPrincipal);
            when(tokenProvider.generateToken(authentication)).thenReturn("new-user-jwt-token");

            AuthenticationResponse response = authenticationService.registerUser(registerRequest);
            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("new-user-jwt-token");

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

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
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("existinguser")
                    .email("new@example.com")
                    .password("password123")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            assertThatThrownBy(() -> authenticationService.registerUser(registerRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceAlreadyExistsException when email exists")
        void register_DuplicateEmail_ThrowsException() {
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username("newuser")
                    .email("existing@example.com")
                    .password("password123")
                    .firstName("Test")
                    .lastName("User")
                    .build();

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authenticationService.registerUser(registerRequest))
                    .isInstanceOf(ResourceAlreadyExistsException.class);

            verify(userRepository, never()).save(any());
        }
    }
}

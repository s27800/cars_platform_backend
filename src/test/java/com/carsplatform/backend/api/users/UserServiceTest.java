package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UserChangePasswordRequest;
import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;
import com.carsplatform.backend.common.security.UserPrincipal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();
    }


    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("should return UserPrincipal when user exists")
        void loadUserByUsername_UserExists_ReturnsUserPrincipal() {

            // Mock repository
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Load user by username
            var result = userService.loadUserByUsername("testuser");

            // Verify result -> user is loaded
            assertThat(result).isInstanceOf(UserPrincipal.class);
            assertThat(result.getUsername()).isEqualTo("testuser");
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("should throw UsernameNotFoundException when user not found")
        void loadUserByUsername_UserNotFound_ThrowsException() {

            // Mock repository
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Load user by non-existing username and verify result -> UsernameNotFoundException is thrown
            assertThatThrownBy(() -> userService.loadUserByUsername("unknown"))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found with username: unknown");
        }
    }


    @Nested
    @DisplayName("loadUserById")
    class LoadUserByIdTests {

        @Test
        @DisplayName("should return UserPrincipal when user exists")
        void loadUserById_UserExists_ReturnsUserPrincipal() {

            // Mock repository
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            // Load user by ID
            var result = userService.loadUserById(testUser.getId());

            // Verify result -> user is loaded
            assertThat(result).isInstanceOf(UserPrincipal.class);
            assertThat(result.getUsername()).isEqualTo("testuser");
            verify(userRepository).findById(testUser.getId());
        }

        @Test
        @DisplayName("should throw UsernameNotFoundException when user not found")
        void loadUserById_UserNotFound_ThrowsException() {

            // Mock repository
            UUID nonExistentId = UUID.randomUUID();
            
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Load user by non-existing ID and verify result -> UsernameNotFoundException is thrown
            assertThatThrownBy(() -> userService.loadUserById(nonExistentId))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("User not found with id: " + nonExistentId);
        }
    }


    @Nested
    @DisplayName("getCurrentUser")
    class GetCurrentUserTests {

        @Test
        @DisplayName("should return current user when authenticated")
        void getCurrentUser_AuthenticatedUser_ReturnsUser() {

            // Mock security context
            mockSecurityContext("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Load current user
            User result = userService.getCurrentUser();

            // Verify result -> current user is loaded
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void getCurrentUser_UserNotFound_ThrowsException() {

            // Mock security context
            mockSecurityContext("unknown");
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Load current user and verify result -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> userService.getCurrentUser())
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }


    @Nested
    @DisplayName("updateUserProfile")
    class UpdateUserProfileTests {

        @Test
        @DisplayName("should update profile successfully with valid data")
        void updateUserProfile_ValidData_UpdatesSuccessfully() {

            // Mock security context
            mockSecurityContext("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // Update user profile
            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName("UpdatedFirstName")
                    .lastName("UpdatedLastName")
                    .email("testuser@example.com") // same email
                    .build();

            userService.updateUserProfile(request);

            // Verify result -> profile is updated
            verify(userMapper).updateEntityFromDto(request, testUser);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("should update profile when changing to new unique email")
        void updateUserProfile_NewUniqueEmail_UpdatesSuccessfully() {

            // Mock security context
            mockSecurityContext("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);

            // Update user profile
            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName("UpdatedFirstName")
                    .lastName("UpdatedLastName")
                    .email("newemail@example.com")
                    .build();

            userService.updateUserProfile(request);

            // Verify result -> profile is updated
            verify(userRepository).existsByEmail("newemail@example.com");
            verify(userMapper).updateEntityFromDto(request, testUser);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("should throw ResourceAlreadyExistsException when email is duplicate")
        void updateUserProfile_DuplicateEmail_ThrowsResourceAlreadyExistsException() {

            // Mock security context
            mockSecurityContext("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

            // Create request with duplicate email
            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName("UpdatedFirstName")
                    .lastName("UpdatedLastName")
                    .email("duplicate@example.com")
                    .build();

            // Update user profile and verify result -> ResourceAlreadyExistsException is thrown
            assertThatThrownBy(() -> userService.updateUserProfile(request))
                    .isInstanceOf(ResourceAlreadyExistsException.class);

            verify(userRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("changeUserPassword")
    class ChangeUserPasswordTests {

        @Test
        @DisplayName("should change password when current password is valid")
        void changeUserPassword_ValidCurrentPassword_ChangesPassword() {

            // Mock security context
            String storedEncodedPassword = testUser.getPassword();
            mockSecurityContext("testuser");

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("currentPassword", storedEncodedPassword)).thenReturn(true);
            when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

            // Change user password
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("currentPassword")
                    .newPassword("newPassword123")
                    .build();

            userService.changeUserPassword(request);

            // Verify result -> password is changed
            verify(passwordEncoder).matches("currentPassword", storedEncodedPassword);
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(testUser);
            assertThat(testUser.getPassword()).isEqualTo("encodedNewPassword");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when current password is invalid")
        void changeUserPassword_InvalidCurrentPassword_ThrowsException() {

            // Mock security context
            String storedEncodedPassword = testUser.getPassword();
            mockSecurityContext("testuser");

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongPassword", storedEncodedPassword)).thenReturn(false);

            // Create request with invalid current password
            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("wrongPassword")
                    .newPassword("newPassword123")
                    .build();

            // Change user password and verify result -> IllegalArgumentException is thrown
            assertThatThrownBy(() -> userService.changeUserPassword(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Current password is incorrect");

            verify(userRepository, never()).save(any());
        }
    }


    // Helper method

    private void mockSecurityContext(String username) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        SecurityContextHolder.setContext(securityContext);
    }
}

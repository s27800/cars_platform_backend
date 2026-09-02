package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UserChangePasswordRequest;
import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;
import com.carsplatform.backend.common.security.UserPrincipal;
import com.carsplatform.backend.common.security.crypto.BlindIndexService;

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

    @Mock
    private BlindIndexService blindIndexService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
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
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            var result = userService.loadUserByUsername("testuser");
            assertThat(result).isInstanceOf(UserPrincipal.class);
            assertThat(result.getUsername()).isEqualTo("testuser");
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("should throw UsernameNotFoundException when user not found")
        void loadUserByUsername_UserNotFound_ThrowsException() {
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

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
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            var result = userService.loadUserById(testUser.getId());
            assertThat(result).isInstanceOf(UserPrincipal.class);
            assertThat(result.getUsername()).isEqualTo("testuser");
            verify(userRepository).findById(testUser.getId());
        }

        @Test
        @DisplayName("should throw UsernameNotFoundException when user not found")
        void loadUserById_UserNotFound_ThrowsException() {
            UUID nonExistentId = UUID.randomUUID();

            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

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
            mockSecurityContext("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            User result = userService.getCurrentUser();
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("testuser");
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void getCurrentUser_UserNotFound_ThrowsException() {
            mockSecurityContext("unknown");
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

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
            mockSecurityContext("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName("UpdatedFirstName")
                    .lastName("UpdatedLastName")
                    .email("testuser@example.com") // same email
                    .build();

            userService.updateUserProfile(request);
            verify(userMapper).updateEntityFromDto(request, testUser);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("should update profile when changing to new unique email")
        void updateUserProfile_NewUniqueEmail_UpdatesSuccessfully() {
            mockSecurityContext("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(blindIndexService.hash("newemail@example.com")).thenReturn("blind-index-of-new-email");
            when(userRepository.existsByEmailHash("blind-index-of-new-email")).thenReturn(false);

            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName("UpdatedFirstName")
                    .lastName("UpdatedLastName")
                    .email("newemail@example.com")
                    .build();

            userService.updateUserProfile(request);
            verify(userRepository).existsByEmailHash("blind-index-of-new-email");
            verify(userMapper).updateEntityFromDto(request, testUser);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("should throw ResourceAlreadyExistsException when email is duplicate")
        void updateUserProfile_DuplicateEmail_ThrowsResourceAlreadyExistsException() {
            mockSecurityContext("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(blindIndexService.hash("duplicate@example.com")).thenReturn("blind-index-of-duplicate-email");
            when(userRepository.existsByEmailHash("blind-index-of-duplicate-email")).thenReturn(true);

            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName("UpdatedFirstName")
                    .lastName("UpdatedLastName")
                    .email("duplicate@example.com")
                    .build();

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
            String storedEncodedPassword = testUser.getPassword();
            mockSecurityContext("testuser");

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("currentPassword", storedEncodedPassword)).thenReturn(true);
            when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("currentPassword")
                    .newPassword("newPassword123")
                    .build();

            userService.changeUserPassword(request);
            verify(passwordEncoder).matches("currentPassword", storedEncodedPassword);
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(testUser);
            assertThat(testUser.getPassword()).isEqualTo("encodedNewPassword");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when current password is invalid")
        void changeUserPassword_InvalidCurrentPassword_ThrowsException() {
            String storedEncodedPassword = testUser.getPassword();
            mockSecurityContext("testuser");

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches("wrongPassword", storedEncodedPassword)).thenReturn(false);

            UserChangePasswordRequest request = UserChangePasswordRequest.builder()
                    .currentPassword("wrongPassword")
                    .newPassword("newPassword123")
                    .build();

            assertThatThrownBy(() -> userService.changeUserPassword(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Current password is incorrect");

            verify(userRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("deleteCurrentUser")
    class DeleteCurrentUserTests {

        @Test
        @DisplayName("should delete current user successfully")
        void deleteCurrentUser_AuthenticatedUser_DeletesSuccessfully() {
            mockSecurityContext("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            userService.deleteCurrentUser();
            verify(userRepository).delete(testUser);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void deleteCurrentUser_UserNotFound_ThrowsException() {
            mockSecurityContext("unknown");
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteCurrentUser())
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).delete(any());
        }
    }


    @org.junit.jupiter.api.AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    private void mockSecurityContext(String username) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        SecurityContextHolder.setContext(securityContext);
    }
}

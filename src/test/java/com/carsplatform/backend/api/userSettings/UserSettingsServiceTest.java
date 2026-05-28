package com.carsplatform.backend.api.userSettings;

import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserService;
import com.carsplatform.backend.api.userSettings.dtos.UpdateUserSettingsRequest;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserSettingsService Unit Tests")
class UserSettingsServiceTest {

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserSettingsService userSettingsService;

    private User testUser;
    private UserSettings testSettings;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("testuser@example.com")
                .build();

        // Create test settings
        testSettings = UserSettings.builder()
                .id(1L)
                .user(testUser)
                .theme("light")
                .build();
    }


    @Nested
    @DisplayName("getCurrentUserSettings Tests")
    class GetCurrentUserSettingsTests {

        @Test
        @DisplayName("returns settings when user has settings")
        void getCurrentUserSettings_UserHasSettings_ReturnsSettings() {

            // Mock user service and repository
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(userSettingsRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testSettings));

            // Get current user settings
            UserSettings result = userSettingsService.getCurrentUserSettings();

            // Verify results -> settings are returned with correct theme
            assertThat(result).isNotNull();
            assertThat(result.getTheme()).isEqualTo("light");

            verify(userService).getCurrentUser();
            verify(userSettingsRepository).findByUserId(testUser.getId());
        }

        @Test
        @DisplayName("throws exception when settings not found")
        void getCurrentUserSettings_SettingsNotFound_ThrowsException() {

            // Mock user service and repository
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(userSettingsRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());

            // Get current user settings and verify results -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> userSettingsService.getCurrentUserSettings())
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }


    @Nested
    @DisplayName("updateCurrentUserSettings Tests")
    class UpdateCurrentUserSettingsTests {

        @Test
        @DisplayName("updates theme successfully")
        void updateCurrentUserSettings_ValidRequest_UpdatesTheme() {

            // Mock user service and repository
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("dark")
                    .build();

            // Mock user service and repository
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(userSettingsRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testSettings));
            when(userSettingsRepository.save(any(UserSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Update current user settings
            UserSettings result = userSettingsService.updateCurrentUserSettings(request);

            // Verify results -> settings are updated with correct theme
            assertThat(result).isNotNull();
            assertThat(result.getTheme()).isEqualTo("dark");

            verify(userSettingsRepository).save(testSettings);
        }

        @Test
        @DisplayName("throws exception when settings not found")
        void updateCurrentUserSettings_SettingsNotFound_ThrowsException() {

            // Mock user service and repository
            UpdateUserSettingsRequest request = UpdateUserSettingsRequest.builder()
                    .theme("dark")
                    .build();

            // Mock user service and repository
            when(userService.getCurrentUser()).thenReturn(testUser);
            when(userSettingsRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());

            // Update current user settings and verify results -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> userSettingsService.updateCurrentUserSettings(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}

package com.carsplatform.backend.api.userSettings;

import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.userSettings.dtos.UserSettingsResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("UserSettingsMapper Tests")
class UserSettingsMapperTest {

    @Autowired
    private UserSettingsMapper userSettingsMapper;


    @Nested
    @DisplayName("toDto Tests")
    class ToDtoTests {

        @Test
        @DisplayName("returns null for null input")
        void toDto_NullInput_ReturnsNull() {

            // Map null settings to DTO
            UserSettingsResponse result = userSettingsMapper.toDto(null);

            // Verify results -> returns null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("maps all fields correctly")
        void toDto_ValidSettings_MapsAllFields() {

            // Create user and settings for testing
            User user = User.builder()
                    .id(1L)
                    .username("testuser")
                    .build();

            UserSettings settings = UserSettings.builder()
                    .id(1L)
                    .user(user)
                    .theme("dark")
                    .build();

            // Map settings to DTO
            UserSettingsResponse result = userSettingsMapper.toDto(settings);

            // Verify results -> all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTheme()).isEqualTo("dark");
        }
    }
}

package com.carsplatform.backend.api.userSettings;

import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.userSettings.dtos.UserSettingsResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

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
            UserSettingsResponse result = userSettingsMapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("maps all fields correctly")
        void toDto_ValidSettings_MapsAllFields() {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .username("testuser")
                    .build();

            UserSettings settings = UserSettings.builder()
                    .id(UUID.randomUUID())
                    .user(user)
                    .theme("dark")
                    .build();

            UserSettingsResponse result = userSettingsMapper.toDto(settings);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(settings.getId());
            assertThat(result.getTheme()).isEqualTo("dark");
        }
    }
}

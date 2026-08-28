package com.carsplatform.backend.api.userSettings;

import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserSettingsRepository Integration Tests")
class UserSettingsRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    private User testUser;
    private UserSettings testSettings;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .username("settingsuser")
                .email("settingsuser@example.com")
                .build();
        entityManager.persist(testUser);

        // Create test settings
        testSettings = TestDataFactory.defaultUserSettings(testUser).build();
        entityManager.persist(testSettings);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("findByUserId Tests")
    class FindByUserIdTests {

        @Test
        @DisplayName("returns settings when user exists")
        void findByUserId_ExistingUser_ReturnsSettings() {

            // Find settings by user ID
            Optional<UserSettings> result = userSettingsRepository.findByUserId(testUser.getId());

            // Verify results -> settings are returned with correct theme
            assertThat(result).isPresent();
            assertThat(result.get().getTheme()).isEqualTo("light");
        }

        @Test
        @DisplayName("returns empty when user does not exist")
        void findByUserId_NonExistingUser_ReturnsEmpty() {

            // Find settings by non-existing user ID
            Optional<UserSettings> result = userSettingsRepository.findByUserId(UUID.randomUUID());

            // Verify results -> empty is returned
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("updates theme successfully")
        void save_UpdateTheme_UpdatesSuccessfully() {

            // Update theme to dark
            testSettings.setTheme("dark");

            UserSettings saved = userSettingsRepository.save(testSettings);

            entityManager.flush();
            entityManager.clear();

            // Find settings by ID
            Optional<UserSettings> result = userSettingsRepository.findById(saved.getId());

            // Verify results -> theme is updated
            assertThat(result).isPresent();
            assertThat(result.get().getTheme()).isEqualTo("dark");
        }
    }
}

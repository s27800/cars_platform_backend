package com.carsplatform.backend.api.users;

import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .username("testuser")
                .email("test@example.com")
                .build();

        entityManager.persist(testUser);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("findByUsername Tests")
    class FindByUsernameTests {

        @Test
        @DisplayName("findByUsername returns user when exists")
        void findByUsername_ExistingUser_ReturnsUser() {

            // Find existing user
            Optional<User> result = userRepository.findByUsername("testuser");

            // Verify result is present and correct
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
            assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("findByUsername returns empty when user does not exist")
        void findByUsername_NonExistingUser_ReturnsEmpty() {

            // Find non-existing user
            Optional<User> result = userRepository.findByUsername("nonexistent");

            // Verify result is empty
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findByUsername is case sensitive")
        void findByUsername_DifferentCase_ReturnsEmpty() {

            // Find user with different case
            Optional<User> result = userRepository.findByUsername("TESTUSER");

            // Verify result is empty
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("existsByUsername Tests")
    class ExistsByUsernameTests {

        @Test
        @DisplayName("existsByUsername returns true for existing username")
        void existsByUsername_ExistingUsername_ReturnsTrue() {

            // Check if username exists
            boolean exists = userRepository.existsByUsername("testuser");

            // Verify result is true
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("existsByUsername returns false for non-existing username")
        void existsByUsername_NonExistingUsername_ReturnsFalse() {

            // Check if username exists
            boolean exists = userRepository.existsByUsername("nonexistent");

            // Verify result is false
            assertThat(exists).isFalse();
        }
    }


    @Nested
    @DisplayName("existsByEmail Tests")
    class ExistsByEmailTests {

        @Test
        @DisplayName("existsByEmail returns true for existing email")
        void existsByEmail_ExistingEmail_ReturnsTrue() {

            // Check if email exists
            boolean exists = userRepository.existsByEmail("test@example.com");

            // Verify result is true
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("existsByEmail returns false for non-existing email")
        void existsByEmail_NonExistingEmail_ReturnsFalse() {

            // Check if email exists
            boolean exists = userRepository.existsByEmail("nonexistent@example.com");

            // Verify result is false
            assertThat(exists).isFalse();
        }
    }


    @Nested
    @DisplayName("JPA Repository inherited methods Tests")
    class JpaRepositoryTests {

        @Test
        @DisplayName("findById returns user when exists")
        void findById_ExistingUser_ReturnsUser() {

            // Find user by ID
            Optional<User> result = userRepository.findById(testUser.getId());

            // Verify result is present and correct
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("save persists new user")
        void save_NewUser_PersistsUser() {

            // Create new user
            User newUser = TestDataFactory.defaultUser()
                    .username("newuser")
                    .email("newuser@example.com")
                    .build();

            User saved = userRepository.save(newUser);
            entityManager.flush();

            // Verify user is saved
            assertThat(saved.getId()).isNotNull();

            User found = entityManager.find(User.class, saved.getId());

            assertThat(found.getUsername()).isEqualTo("newuser");
        }

        @Test
        @DisplayName("delete removes user")
        void delete_ExistingUser_RemovesUser() {

            // Delete user
            userRepository.delete(testUser);
            entityManager.flush();

            // Verify user is deleted
            User found = entityManager.find(User.class, testUser.getId());

            assertThat(found).isNull();
        }

        @Test
        @DisplayName("count returns correct number of users")
        void count_MultipleUsers_ReturnsCorrectCount() {

            // Create new user
            User user2 = TestDataFactory.createUser("2");
            
            entityManager.persist(user2);
            entityManager.flush();

            // Count users
            long count = userRepository.count();

            // Verify count is correct
            assertThat(count).isEqualTo(2);
        }
    }
}

package com.carsplatform.backend.api.users;

import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.security.crypto.BlindIndexService;

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

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlindIndexService blindIndexService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.defaultUser()
                .username("testuser")
                .email("test@example.com")
                .build();

        entityManager.persist(testUser);

        entityManager.flush();
    }


    @Nested
    @DisplayName("findByUsername Tests")
    class FindByUsernameTests {

        @Test
        @DisplayName("findByUsername returns user when exists")
        void findByUsername_ExistingUser_ReturnsUser() {
            Optional<User> result = userRepository.findByUsername("testuser");
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
            assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("findByUsername returns empty when user does not exist")
        void findByUsername_NonExistingUser_ReturnsEmpty() {
            Optional<User> result = userRepository.findByUsername("nonexistent");
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findByUsername is case sensitive")
        void findByUsername_DifferentCase_ReturnsEmpty() {
            Optional<User> result = userRepository.findByUsername("TESTUSER");
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("existsByUsername Tests")
    class ExistsByUsernameTests {

        @Test
        @DisplayName("existsByUsername returns true for existing username")
        void existsByUsername_ExistingUsername_ReturnsTrue() {
            boolean exists = userRepository.existsByUsername("testuser");
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("existsByUsername returns false for non-existing username")
        void existsByUsername_NonExistingUsername_ReturnsFalse() {
            boolean exists = userRepository.existsByUsername("nonexistent");
            assertThat(exists).isFalse();
        }
    }


    @Nested
    @DisplayName("existsByEmailHash Tests")
    class ExistsByEmailHashTests {

        @Test
        @DisplayName("existsByEmailHash returns true for existing email")
        void existsByEmailHash_ExistingEmail_ReturnsTrue() {
            boolean exists = userRepository.existsByEmailHash(blindIndexService.hash("test@example.com"));
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("existsByEmailHash ignores case and surrounding spaces")
        void existsByEmailHash_DifferentCase_ReturnsTrue() {
            boolean exists = userRepository.existsByEmailHash(blindIndexService.hash("  Test@Example.COM  "));
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("existsByEmailHash returns false for non-existing email")
        void existsByEmailHash_NonExistingEmail_ReturnsFalse() {
            boolean exists = userRepository.existsByEmailHash(blindIndexService.hash("nonexistent@example.com"));
            assertThat(exists).isFalse();
        }
    }


    @Nested
    @DisplayName("JPA Repository inherited methods Tests")
    class JpaRepositoryTests {

        @Test
        @DisplayName("findById returns user when exists")
        void findById_ExistingUser_ReturnsUser() {
            Optional<User> result = userRepository.findById(testUser.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("save persists new user")
        void save_NewUser_PersistsUser() {
            User newUser = TestDataFactory.defaultUser()
                    .username("newuser")
                    .email("newuser@example.com")
                    .build();

            User saved = userRepository.save(newUser);
            entityManager.flush();

            assertThat(saved.getId()).isNotNull();

            User found = entityManager.find(User.class, saved.getId());

            assertThat(found.getUsername()).isEqualTo("newuser");
        }

        @Test
        @DisplayName("delete removes user")
        void delete_ExistingUser_RemovesUser() {
            userRepository.delete(testUser);
            entityManager.flush();

            User found = entityManager.find(User.class, testUser.getId());

            assertThat(found).isNull();
        }

        @Test
        @DisplayName("count returns correct number of users")
        void count_MultipleUsers_ReturnsCorrectCount() {
            User user2 = TestDataFactory.createUser("2");

            entityManager.persist(user2);
            entityManager.flush();

            long count = userRepository.count();

            assertThat(count).isEqualTo(2);
        }
    }
}

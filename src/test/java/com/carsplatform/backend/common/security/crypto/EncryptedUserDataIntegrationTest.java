package com.carsplatform.backend.common.security.crypto;

import com.carsplatform.backend.api.authentication.AuthenticationService;
import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Encrypted user data Integration Tests")
class EncryptedUserDataIntegrationTest {

    private static final String USERNAME = "cryptouser";
    private static final String EMAIL = "crypto.user@example.com";
    private static final String FIRST_NAME = "Krystyna";
    private static final String LAST_NAME = "Szyfrowska";

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlindIndexService blindIndexService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    void registerAccount() {
        authenticationService.registerUser(RegisterRequest.builder()
                .username(USERNAME)
                .email(EMAIL)
                .password("Password123")
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build());

        entityManager.flush();
    }

    private Map<String, Object> storedRow() {
        return jdbcTemplate.queryForMap(
                "SELECT email, email_hash, first_name, last_name FROM users WHERE username = ?",
                USERNAME);
    }


    @Test
    @DisplayName("stores personal data unreadable in the database")
    void registeredAccount_IsStoredEncrypted() {
        Map<String, Object> row = storedRow();

        assertThat((String) row.get("email")).startsWith("v1:").doesNotContain(EMAIL);
        assertThat((String) row.get("first_name")).startsWith("v1:").doesNotContain(FIRST_NAME);
        assertThat((String) row.get("last_name")).startsWith("v1:").doesNotContain(LAST_NAME);
    }

    @Test
    @DisplayName("leaves the username and the password hash unchanged")
    void registeredAccount_KeepsUsernameAndPasswordHashUnchanged() {
        String password = jdbcTemplate.queryForObject(
                "SELECT password FROM users WHERE username = ?", String.class, USERNAME);

        assertThat(password).startsWith("$2a$").doesNotStartWith("v1:");
    }

    @Test
    @DisplayName("reads the personal data back in the clear")
    void registeredAccount_IsReadBackDecrypted() {
        entityManager.clear();

        Optional<User> user = userRepository.findByUsername(USERNAME);

        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo(EMAIL);
        assertThat(user.get().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(user.get().getLastName()).isEqualTo(LAST_NAME);
    }

    @Test
    @DisplayName("writes the blind index next to the encrypted address")
    void registeredAccount_HasBlindIndexMatchingTheAddress() {
        assertThat((String) storedRow().get("email_hash"))
                .isEqualTo(blindIndexService.hash(EMAIL))
                .hasSize(64);
    }

    @Test
    @DisplayName("rejects the same address registered in another case")
    void register_SameAddressInAnotherCase_IsRejected() {
        RegisterRequest duplicate = RegisterRequest.builder()
                .username("someoneelse")
                .email(EMAIL.toUpperCase())
                .password("Password123")
                .firstName("Jan")
                .lastName("Kowalski")
                .build();

        assertThatThrownBy(() -> authenticationService.registerUser(duplicate))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("rewrites the blind index when the address changes")
    void updatedAddress_UpdatesTheBlindIndex() {
        User user = userRepository.findByUsername(USERNAME).orElseThrow();

        user.setEmail("moved@example.com");
        userRepository.saveAndFlush(user);

        assertThat((String) storedRow().get("email_hash"))
                .isEqualTo(blindIndexService.hash("moved@example.com"));
    }

    @Test
    @DisplayName("stores a shared first name as two different ciphertexts")
    void twoAccountsWithTheSameFirstName_AreStoredDifferently() {
        authenticationService.registerUser(RegisterRequest.builder()
                .username("cryptotwin")
                .email("crypto.twin@example.com")
                .password("Password123")
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build());

        entityManager.flush();

        String twin = jdbcTemplate.queryForObject(
                "SELECT first_name FROM users WHERE username = ?", String.class, "cryptotwin");

        assertThat(twin).isNotEqualTo((String) storedRow().get("first_name"));
    }
}

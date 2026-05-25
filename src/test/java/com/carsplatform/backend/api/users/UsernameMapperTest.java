package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UsernameResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;


@DisplayName("UsernameMapper Tests")
class UsernameMapperTest {

    private final UsernameMapper mapper = Mappers.getMapper(UsernameMapper.class);

    
    @Nested
    @DisplayName("toDto Tests")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            UsernameResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map username correctly")
        void toDto_ValidUser_MapsUsername() {

            // Create test user
            User user = TestDataFactory.defaultUser()
                    .username("john_doe")
                    .build();

            // Map valid user
            UsernameResponse result = mapper.toDto(user);

            // Verify username is mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("john_doe");
        }

        @Test
        @DisplayName("should map null username")
        void toDto_UserWithNullUsername_MapsNullUsername() {

            // Create test user with null username
            User user = TestDataFactory.defaultUser()
                    .username(null)
                    .build();

            // Map user with null username
            UsernameResponse result = mapper.toDto(user);

            // Verify username is null
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isNull();
        }
    }
}

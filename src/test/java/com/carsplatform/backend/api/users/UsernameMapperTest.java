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
            UsernameResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map username correctly")
        void toDto_ValidUser_MapsUsername() {
            User user = TestDataFactory.defaultUser()
                    .username("john_doe")
                    .build();

            UsernameResponse result = mapper.toDto(user);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("john_doe");
        }

        @Test
        @DisplayName("should map null username")
        void toDto_UserWithNullUsername_MapsNullUsername() {
            User user = TestDataFactory.defaultUser()
                    .username(null)
                    .build();

            UsernameResponse result = mapper.toDto(user);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isNull();
        }
    }
}

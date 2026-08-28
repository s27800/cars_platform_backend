package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.api.users.dtos.UserResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("UserMapper Tests")
class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User testUser;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("testuser@example.com")
                .firstName("Test")
                .lastName("User")
                .isAdmin(false)
                .build();
    }


    @Nested
    @DisplayName("toResponseDto")
    class ToResponseDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toResponseDto_NullInput_ReturnsNull() {

            // Map null input
            UserResponse result = userMapper.toResponseDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toResponseDto_ValidUser_MapsAllFields() {

            // Map valid user
            UserResponse result = userMapper.toResponseDto(testUser);

            // Verify all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testUser.getId());
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getEmail()).isEqualTo("testuser@example.com");
            assertThat(result.getFirstName()).isEqualTo("Test");
            assertThat(result.getLastName()).isEqualTo("User");
            assertThat(result.getIsAdmin()).isFalse();
        }

        @Test
        @DisplayName("should map admin user correctly")
        void toResponseDto_AdminUser_MapsIsAdminTrue() {

            // Create admin user
            User adminUser = TestDataFactory.adminUser()
                    .id(UUID.randomUUID())
                    .build();

            // Map admin user
            UserResponse result = userMapper.toResponseDto(adminUser);

            // Verify isAdmin is true
            assertThat(result.getIsAdmin()).isTrue();
        }
    }


    @Nested
    @DisplayName("updateEntityFromDto")
    class UpdateEntityFromDtoTests {

        @Test
        @DisplayName("should update entity fields from DTO")
        void updateEntityFromDto_ValidRequest_UpdatesEntity() {

            // Create request with updated fields
            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName("UpdatedFirst")
                    .lastName("UpdatedLast")
                    .email("updated@example.com")
                    .build();

            // Update entity
            userMapper.updateEntityFromDto(request, testUser);

            // Verify updated fields are correct
            assertThat(testUser.getFirstName()).isEqualTo("UpdatedFirst");
            assertThat(testUser.getLastName()).isEqualTo("UpdatedLast");
            assertThat(testUser.getEmail()).isEqualTo("updated@example.com");
            assertThat(testUser.getUsername()).isEqualTo("testuser");
        }

        @Test
        @DisplayName("should handle null values in request")
        void updateEntityFromDto_NullFieldsInRequest_UpdatesToNull() {

            // Create request without null fields
            UserModifyRequest request = UserModifyRequest.builder()
                    .firstName(null)
                    .lastName(null)
                    .email(null)
                    .build();

            // Update entity with null fields in request
            userMapper.updateEntityFromDto(request, testUser);

            // Verify fields are updated to null
            assertThat(testUser.getFirstName()).isNull();
            assertThat(testUser.getLastName()).isNull();
            assertThat(testUser.getEmail()).isNull();
        }
    }
}

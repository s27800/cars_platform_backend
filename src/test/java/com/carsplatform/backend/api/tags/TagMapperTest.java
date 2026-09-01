package com.carsplatform.backend.api.tags;

import com.carsplatform.backend.api.tags.dtos.CarTagResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("TagMapper Tests")
class TagMapperTest {

    private final TagMapper mapper = Mappers.getMapper(TagMapper.class);

    private Tag testTag;

    @BeforeEach
    void setUp() {
        testTag = TestDataFactory.defaultTag()
                .id(UUID.randomUUID())
                .name("Electric")
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            CarTagResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map id and name correctly")
        void toDto_ValidTag_MapsAllFields() {
            CarTagResponse result = mapper.toDto(testTag);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testTag.getId());
            assertThat(result.getName()).isEqualTo("Electric");
        }
    }


    @Nested
    @DisplayName("toDtoList")
    class ToDtoListTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDtoList_NullInput_ReturnsNull() {
            Set<CarTagResponse> result = mapper.toDtoList(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return empty set when input is empty")
        void toDtoList_EmptySet_ReturnsEmptySet() {
            Set<CarTagResponse> result = mapper.toDtoList(new HashSet<>());
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map all tags correctly")
        void toDtoList_SetWithTags_MapsAllTags() {
            Tag tag1 = TestDataFactory.defaultTag().id(UUID.randomUUID()).name("Electric").build();
            Tag tag2 = TestDataFactory.defaultTag().id(UUID.randomUUID()).name("Hybrid").build();
            Tag tag3 = TestDataFactory.defaultTag().id(UUID.randomUUID()).name("SUV").build();
            Set<Tag> tags = Set.of(tag1, tag2, tag3);

            Set<CarTagResponse> result = mapper.toDtoList(tags);
            assertThat(result).hasSize(3);
            assertThat(result).extracting(CarTagResponse::getName)
                    .containsExactlyInAnyOrder("Electric", "Hybrid", "SUV");
        }
    }
}

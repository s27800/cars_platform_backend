package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.generations.dtos.CarGenerationResponse;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("GenerationMapper Tests")
class GenerationMapperTest {

    private final GenerationMapper mapper = Mappers.getMapper(GenerationMapper.class);

    private Generation testGeneration;

    @BeforeEach
    void setUp() {
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .build();
        testGeneration = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .name("G20")
                .build();
    }


    @Nested
    @DisplayName("toDto Tests")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            CarGenerationResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map id and name correctly")
        void toDto_ValidGeneration_MapsAllFields() {
            CarGenerationResponse result = mapper.toDto(testGeneration);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testGeneration.getId());
            assertThat(result.getName()).isEqualTo("G20");
        }

        @Test
        @DisplayName("should map different generation names")
        void toDto_DifferentGenerations_MapsCorrectly() {
            Brand brand = TestDataFactory.defaultBrand().build();
            Model model = TestDataFactory.defaultModel(brand).build();

            Generation gen1 = TestDataFactory.defaultGeneration(model)
                    .id(UUID.randomUUID())
                    .name("First Generation")
                    .build();

            Generation gen2 = TestDataFactory.defaultGeneration(model)
                    .id(UUID.randomUUID())
                    .name("Second Generation")
                    .build();

            CarGenerationResponse result1 = mapper.toDto(gen1);
            CarGenerationResponse result2 = mapper.toDto(gen2);
            assertThat(result1.getName()).isEqualTo("First Generation");
            assertThat(result2.getName()).isEqualTo("Second Generation");
        }
    }
}

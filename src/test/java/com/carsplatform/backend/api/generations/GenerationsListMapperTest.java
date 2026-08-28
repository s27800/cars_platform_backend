package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.generations.dtos.GenerationsListResponse;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("GenerationsListMapper Tests")
class GenerationsListMapperTest {

    private final GenerationsListMapper mapper = Mappers.getMapper(GenerationsListMapper.class);

    private Generation testGeneration;
    private Model testModel;

    @BeforeEach
    void setUp() {

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();

        // Create test model
        testModel = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .generations(new ArrayList<>())
                .build();

        // Create test generation
        testGeneration = TestDataFactory.defaultGeneration(testModel)
                .id(UUID.randomUUID())
                .name("G20")
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            GenerationsListResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map id and name correctly")
        void toDto_ValidGeneration_MapsAllFields() {

            // Map valid generation
            GenerationsListResponse result = mapper.toDto(testGeneration);

            // Verify results -> correct id and name are mapped
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testGeneration.getId());
            assertThat(result.getName()).isEqualTo("G20");
        }
    }


    @Nested
    @DisplayName("map (from Model)")
    class MapFromModelTests {

        @Test
        @DisplayName("should return null when model is null")
        void map_NullModel_ReturnsNull() {

            // Map null model
            List<GenerationsListResponse> result = mapper.map(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return empty list when model has no generations")
        void map_ModelWithNoGenerations_ReturnsEmptyList() {

            // Set model with empty generations list
            testModel.setGenerations(new ArrayList<>());

            // Map model
            List<GenerationsListResponse> result = mapper.map(testModel);

            // Verify result is empty
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map all generations from model")
        void map_ModelWithGenerations_MapsAllGenerations() {

            // Create model with multiple generations
            Generation gen1 = TestDataFactory.defaultGeneration(testModel).id(UUID.randomUUID()).name("Gen I").build();
            Generation gen2 = TestDataFactory.defaultGeneration(testModel).id(UUID.randomUUID()).name("Gen II").build();

            testModel.setGenerations(List.of(gen1, gen2));

            // Map model
            List<GenerationsListResponse> result = mapper.map(testModel);

            // Verify results -> all generations mapped correctly
            assertThat(result).hasSize(2);
            assertThat(result).extracting(GenerationsListResponse::getName)
                    .containsExactly("Gen I", "Gen II");
        }
    }
}

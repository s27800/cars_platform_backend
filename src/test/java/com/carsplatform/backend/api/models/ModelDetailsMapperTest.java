package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.dtos.ModelDetailsResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ModelDetailsMapper Integration Tests")
class ModelDetailsMapperTest {

    @Autowired
    private ModelDetailsMapper mapper;

    private Brand testBrand;
    private Model testModel;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = TestDataFactory.defaultBrand()
                .id(1)
                .build();

        // Create test model
        testModel = TestDataFactory.defaultModel(testBrand)
                .id(1)
                .name("3 Series")
                .generations(List.of())
                .build();
    }


    @Nested
    @DisplayName("toDto Tests")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            ModelDetailsResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map model basic fields correctly")
        void toDto_ValidModel_MapsBasicFields() {

            // Map valid model
            ModelDetailsResponse result = mapper.toDto(testModel);

            // Verify basic fields
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getName()).isEqualTo("3 Series");
        }

        @Test
        @DisplayName("should map brand information")
        void toDto_ValidModel_MapsBrandInfo() {

            // Set brand with logo
            testBrand.setLogoUrl("https://example.com/logo.png");

            // Map model
            ModelDetailsResponse result = mapper.toDto(testModel);

            // Verify brand is mapped correctly
            assertThat(result.getBrand()).isNotNull();
            assertThat(result.getBrand().getId()).isEqualTo(testBrand.getId());
            assertThat(result.getBrand().getName()).isEqualTo(testBrand.getName());
            assertThat(result.getBrand().getLogoUrl()).isEqualTo("https://example.com/logo.png");
        }

        @Test
        @DisplayName("should map empty generations list")
        void toDto_ModelWithEmptyGenerations_MapsEmptyList() {

            // Create model with empty generations list
            testModel.setGenerations(List.of());

            // Map model with empty generations list
            ModelDetailsResponse result = mapper.toDto(testModel);

            // Verify generations list is empty
            assertThat(result.getGenerations()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should map generations list")
        void toDto_ModelWithGenerations_MapsGenerationsList() {

            // Create model with generations
            Generation gen1 = TestDataFactory.defaultGeneration(testModel)
                    .id(1)
                    .name("E90")
                    .cars(List.of())
                    .build();

            Generation gen2 = TestDataFactory.defaultGeneration(testModel)
                    .id(2)
                    .name("F30")
                    .cars(List.of())
                    .build();

            testModel.setGenerations(List.of(gen1, gen2));

            // Map model with generations
            ModelDetailsResponse result = mapper.toDto(testModel);

            // Verify generations list is mapped correctly
            assertThat(result.getGenerations()).hasSize(2);
            assertThat(result.getGenerations().get(0).getName()).isEqualTo("E90");
            assertThat(result.getGenerations().get(0).getCarsCount()).isEqualTo(0);
            assertThat(result.getGenerations().get(1).getName()).isEqualTo("F30");
            assertThat(result.getGenerations().get(1).getCarsCount()).isEqualTo(0);
        }
    }
}

package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.dtos.ModelsListResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DisplayName("ModelsListMapper Tests")
class ModelsListMapperTest {

    private final ModelsListMapper mapper = Mappers.getMapper(ModelsListMapper.class);

    private Model testModel;
    private Brand testBrand;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = Brand.builder()
                .id(1)
                .name("BMW")
                .country("Germany")
                .models(new ArrayList<>())
                .build();

        // Create test model
        testModel = Model.builder()
                .id(1)
                .name("3 Series")
                .description("Compact executive car")
                .brand(testBrand)
                .generations(new ArrayList<>())
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            ModelsListResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map id, name and generationsCount")
        void toDto_ValidModel_MapsIdNameAndGenerationsCount() {

            // Add generations to test model
            Generation gen1 = Generation.builder().id(1).name("E90").model(testModel).build();
            Generation gen2 = Generation.builder().id(2).name("F30").model(testModel).build();

            testModel.setGenerations(List.of(gen1, gen2));

            // Map valid model
            ModelsListResponse result = mapper.toDto(testModel);

            // Verify result is mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getName()).isEqualTo("3 Series");
            assertThat(result.getGenerationsCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("should return 0 generations count when model has no generations")
        void toDto_ModelWithNoGenerations_ReturnsZeroCount() {

            // Map model with empty generations
            ModelsListResponse result = mapper.toDto(testModel);

            // Verify generationsCount is 0
            assertThat(result).isNotNull();
            assertThat(result.getGenerationsCount()).isEqualTo(0);
        }
    }


    @Nested
    @DisplayName("map (from Brand)")
    class MapFromBrandTests {

        @Test
        @DisplayName("should return empty list when brand is null")
        void map_NullBrand_ReturnsEmptyList() {

            // Map null brand
            List<ModelsListResponse> result = mapper.map(null);

            // Verify result is empty
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty list when brand has no models")
        void map_BrandWithNoModels_ReturnsEmptyList() {

            // Set empty models list for brand
            testBrand.setModels(new ArrayList<>());

            // Map brand with no models
            List<ModelsListResponse> result = mapper.map(testBrand);

            // Verify result is empty
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map all models from brand with generationsCount")
        void map_BrandWithModels_MapsAllModels() {

            // Create test models with generations
            Model model1 = Model.builder().id(1).name("3 Series").generations(new ArrayList<>()).build();
            Model model2 = Model.builder().id(2).name("5 Series").generations(new ArrayList<>()).build();
            Model model3 = Model.builder().id(3).name("X5").generations(new ArrayList<>()).build();

            // Add generations to model1
            model1.getGenerations().add(Generation.builder().id(1).name("E90").build());
            model1.getGenerations().add(Generation.builder().id(2).name("F30").build());

            testBrand.setModels(List.of(model1, model2, model3));

            // Map brand with models
            List<ModelsListResponse> result = mapper.map(testBrand);

            // Verify result -> all models are mapped correctly
            assertThat(result).hasSize(3);
            assertThat(result).extracting(ModelsListResponse::getName)
                    .containsExactly("3 Series", "5 Series", "X5");
            assertThat(result.get(0).getGenerationsCount()).isEqualTo(2);
            assertThat(result.get(1).getGenerationsCount()).isEqualTo(0);
            assertThat(result.get(2).getGenerationsCount()).isEqualTo(0);
        }
    }
}

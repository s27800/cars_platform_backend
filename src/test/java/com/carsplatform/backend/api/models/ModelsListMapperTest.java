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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("ModelsListMapper Tests")
class ModelsListMapperTest {

    private final ModelsListMapper mapper = Mappers.getMapper(ModelsListMapper.class);

    private Model testModel;
    private Brand testBrand;

    @BeforeEach
    void setUp() {
        testBrand = Brand.builder()
                .id(UUID.randomUUID())
                .name("BMW")
                .country("Germany")
                .models(new ArrayList<>())
                .build();
        testModel = Model.builder()
                .id(UUID.randomUUID())
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
            ModelsListResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map id, name and generationsCount")
        void toDto_ValidModel_MapsIdNameAndGenerationsCount() {
            Generation gen1 = Generation.builder().id(UUID.randomUUID()).name("E90").model(testModel).build();
            Generation gen2 = Generation.builder().id(UUID.randomUUID()).name("F30").model(testModel).build();

            testModel.setGenerations(List.of(gen1, gen2));

            ModelsListResponse result = mapper.toDto(testModel);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testModel.getId());
            assertThat(result.getName()).isEqualTo("3 Series");
            assertThat(result.getGenerationsCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("should return 0 generations count when model has no generations")
        void toDto_ModelWithNoGenerations_ReturnsZeroCount() {
            ModelsListResponse result = mapper.toDto(testModel);

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
            List<ModelsListResponse> result = mapper.map(null);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty list when brand has no models")
        void map_BrandWithNoModels_ReturnsEmptyList() {
            testBrand.setModels(new ArrayList<>());

            List<ModelsListResponse> result = mapper.map(testBrand);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map all models from brand with generationsCount")
        void map_BrandWithModels_MapsAllModels() {
            Model model1 = Model.builder().id(UUID.randomUUID()).name("3 Series").generations(new ArrayList<>()).build();
            Model model2 = Model.builder().id(UUID.randomUUID()).name("5 Series").generations(new ArrayList<>()).build();
            Model model3 = Model.builder().id(UUID.randomUUID()).name("X5").generations(new ArrayList<>()).build();

            model1.getGenerations().add(Generation.builder().id(UUID.randomUUID()).name("E90").build());
            model1.getGenerations().add(Generation.builder().id(UUID.randomUUID()).name("F30").build());

            testBrand.setModels(List.of(model1, model2, model3));

            List<ModelsListResponse> result = mapper.map(testBrand);
            assertThat(result).hasSize(3);
            assertThat(result).extracting(ModelsListResponse::getName)
                    .containsExactly("3 Series", "5 Series", "X5");
            assertThat(result.get(0).getGenerationsCount()).isEqualTo(2);
            assertThat(result.get(1).getGenerationsCount()).isEqualTo(0);
            assertThat(result.get(2).getGenerationsCount()).isEqualTo(0);
        }
    }
}

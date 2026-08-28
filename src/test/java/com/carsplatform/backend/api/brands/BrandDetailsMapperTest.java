package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandDetailsResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("BrandDetailsMapper Tests")
class BrandDetailsMapperTest {

    @Autowired
    private BrandDetailsMapper mapper;

    private Brand testBrand;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = Brand.builder()
                .id(UUID.randomUUID())
                .name("BMW")
                .country("Germany")
                .foundedYear(1916)
                .description("Bavarian Motor Works - luxury vehicle manufacturer")
                .models(new ArrayList<>())
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class toDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            BrandDetailsResponse result = mapper.toDto(null);

            // Verify result -> null is returned
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all basic fields correctly")
        void toDto_ValidBrand_MapsAllBasicFields() {

            // Map valid brand
            BrandDetailsResponse result = mapper.toDto(testBrand);

            // Verify result -> all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testBrand.getId());
            assertThat(result.getName()).isEqualTo("BMW");
            assertThat(result.getCountry()).isEqualTo("Germany");
            assertThat(result.getFoundedYear()).isEqualTo(1916);
            assertThat(result.getDescription()).isEqualTo("Bavarian Motor Works - luxury vehicle manufacturer");
        }

        @Test
        @DisplayName("should map empty models list")
        void toDto_BrandWithNoModels_MapsEmptyList() {

            // Set empty models list
            testBrand.setModels(new ArrayList<>());

            // Map brand with empty models list
            BrandDetailsResponse result = mapper.toDto(testBrand);

            // Verify result -> empty list is returned
            assertThat(result.getModels()).isEmpty();
        }

        @Test
        @DisplayName("should map models list with items and generationsCount")
        void toDto_BrandWithModels_MapsModelsList() {

            // Create test models and generations for brand
            Model model1 = Model.builder()
                    .id(UUID.randomUUID())
                    .name("3 Series")
                    .brand(testBrand)
                    .generations(new ArrayList<>())
                    .build();

            Model model2 = Model.builder()
                    .id(UUID.randomUUID())
                    .name("5 Series")
                    .brand(testBrand)
                    .generations(new ArrayList<>())
                    .build();

            Generation gen1 = Generation.builder().id(UUID.randomUUID()).name("E90").model(model1).build();
            Generation gen2 = Generation.builder().id(UUID.randomUUID()).name("F30").model(model1).build();

            model1.getGenerations().add(gen1);
            model1.getGenerations().add(gen2);

            testBrand.setModels(List.of(model1, model2));

            // Map brand with models list
            BrandDetailsResponse result = mapper.toDto(testBrand);

            // Verify result -> models list is mapped correctly with generationsCount
            assertThat(result.getModels()).hasSize(2);
            assertThat(result.getModels().get(0).getId()).isEqualTo(model1.getId());
            assertThat(result.getModels().get(0).getName()).isEqualTo("3 Series");
            assertThat(result.getModels().get(0).getGenerationsCount()).isEqualTo(2);
            assertThat(result.getModels().get(1).getId()).isEqualTo(model2.getId());
            assertThat(result.getModels().get(1).getName()).isEqualTo("5 Series");
            assertThat(result.getModels().get(1).getGenerationsCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("should handle brand with null models list")
        void toDto_BrandWithNullModels_HandlesNullGracefully() {

            // Set null models list
            testBrand.setModels(null);

            // Map brand with null models list
            BrandDetailsResponse result = mapper.toDto(testBrand);

            // Verify result -> empty list is returned
            assertThat(result).isNotNull();
            assertThat(result.getModels()).isEmpty();
        }

        @Test
        @DisplayName("should handle null foundedYear")
        void toDto_BrandWithNullFoundedYear_MapsCorrectly() {

            // Set null foundedYear
            testBrand.setFoundedYear(null);

            // Map brand with null foundedYear
            BrandDetailsResponse result = mapper.toDto(testBrand);

            // Verify result -> foundedYear is null
            assertThat(result.getFoundedYear()).isNull();
        }
    }
}

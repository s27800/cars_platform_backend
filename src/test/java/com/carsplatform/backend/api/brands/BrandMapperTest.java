package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.dtos.CarBrandResponse;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("BrandMapper Tests")
class BrandMapperTest {

    private final BrandMapper mapper = Mappers.getMapper(BrandMapper.class);

    private Brand testBrand;
    private Car testCar;

    @BeforeEach
    void setUp() {

        // Create test data for car
        testBrand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("BMW")
                .country("Germany")
                .foundedYear(1916)
                .description("Bavarian Motor Works")
                .build();

        Model model = TestDataFactory.defaultModel(testBrand).id(UUID.randomUUID()).build();
        Generation generation = TestDataFactory.defaultGeneration(model).id(UUID.randomUUID()).build();
        BodyType bodyType = TestDataFactory.defaultBodyType().id(UUID.randomUUID()).build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .images(List.of())
                .tags(new HashSet<>())
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            CarBrandResponse result = mapper.toDto(null);

            // Verify result -> null is returned
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidBrand_MapsAllFields() {

            // Map valid brand
            CarBrandResponse result = mapper.toDto(testBrand);

            // Verify result -> all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testBrand.getId());
            assertThat(result.getName()).isEqualTo("BMW");
            assertThat(result.getCountry()).isEqualTo("Germany");
            assertThat(result.getFoundedYear()).isEqualTo(1916);
            assertThat(result.getDescription()).isEqualTo("Bavarian Motor Works");
        }
    }


    @Nested
    @DisplayName("map (from Car)")
    class MapFromCarTests {

        @Test
        @DisplayName("should return null when car is null")
        void map_NullCar_ReturnsNull() {

            // Map null car
            CarBrandResponse result = mapper.map(null);

            // Verify result -> null is returned
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should extract brand from car's generation->model->brand")
        void map_ValidCar_ExtractsBrand() {

            // Map valid car
            CarBrandResponse result = mapper.map(testCar);

            // Verify result -> brand fields are extracted correctly
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("BMW");
        }
    }
}

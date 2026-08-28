package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.dtos.CarModelResponse;
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


@DisplayName("ModelMapper Tests")
class ModelMapperTest {

    private final ModelMapper mapper = Mappers.getMapper(ModelMapper.class);

    private Model testModel;
    private Car testCar;

    @BeforeEach
    void setUp() {

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("BMW")
                .build();

        // Create test model
        testModel = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .name("3 Series")
                .description("Compact executive car")
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(testModel)
                .id(UUID.randomUUID())
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();

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
            CarModelResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidModel_MapsAllFields() {

            // Map valid model
            CarModelResponse result = mapper.toDto(testModel);

            // Verify result is mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testModel.getId());
            assertThat(result.getName()).isEqualTo("3 Series");
            assertThat(result.getDescription()).isEqualTo("Compact executive car");
        }
    }


    @Nested
    @DisplayName("map (from Car)")
    class MapFromCarTests {

        @Test
        @DisplayName("should return null when car is null")
        void map_NullCar_ReturnsNull() {

            // Map null input
            CarModelResponse result = mapper.map(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should extract model from car's generation->model")
        void map_ValidCar_ExtractsModel() {

            // Map valid car
            CarModelResponse result = mapper.map(testCar);

            // Verify result is mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("3 Series");
        }
    }
}

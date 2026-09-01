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
import java.util.LinkedHashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("ModelMapper Tests")
class ModelMapperTest {

    private final ModelMapper mapper = Mappers.getMapper(ModelMapper.class);

    private Model testModel;
    private Car testCar;

    @BeforeEach
    void setUp() {
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("BMW")
                .build();
        testModel = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .name("3 Series")
                .description("Compact executive car")
                .build();
        Generation generation = TestDataFactory.defaultGeneration(testModel)
                .id(UUID.randomUUID())
                .build();
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .images(new LinkedHashSet<>())
                .tags(new HashSet<>())
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            CarModelResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidModel_MapsAllFields() {
            CarModelResponse result = mapper.toDto(testModel);
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
            CarModelResponse result = mapper.map(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should extract model from car's generation->model")
        void map_ValidCar_ExtractsModel() {
            CarModelResponse result = mapper.map(testCar);
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("3 Series");
        }
    }
}

package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.engines.Engine;
import com.carsplatform.backend.api.generations.dtos.GenerationDetailsResponse;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.transmissions.Transmission;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("GenerationDetailsMapper Integration Tests")
class GenerationDetailsMapperTest {

    @Autowired
    private GenerationDetailsMapper mapper;

    private Generation testGeneration;
    private Model testModel;
    private Brand testBrand;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();

        // Create test model
        testModel = TestDataFactory.defaultModel(testBrand)
                .id(UUID.randomUUID())
                .name("3 Series")
                .build();

        // Create test generation
        testGeneration = TestDataFactory.defaultGeneration(testModel)
                .id(UUID.randomUUID())
                .name("E90")
                .cars(List.of())
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            GenerationDetailsResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map generation basic fields correctly")
        void toDto_ValidGeneration_MapsBasicFields() {

            // Map valid generation
            GenerationDetailsResponse result = mapper.toDto(testGeneration);

            // Verify result is mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testGeneration.getId());
            assertThat(result.getName()).isEqualTo("E90");
        }

        @Test
        @DisplayName("should map model correctly")
        void toDto_ValidGeneration_MapsModel() {

            // Map valid generation
            GenerationDetailsResponse result = mapper.toDto(testGeneration);

            // Verify model is mapped correctly
            assertThat(result.getModel()).isNotNull();
            assertThat(result.getModel().getId()).isEqualTo(testModel.getId());
            assertThat(result.getModel().getName()).isEqualTo("3 Series");
        }

        @Test
        @DisplayName("should map brand correctly")
        void toDto_ValidGeneration_MapsBrand() {

            // Map valid generation
            GenerationDetailsResponse result = mapper.toDto(testGeneration);

            // Verify brand is mapped correctly
            assertThat(result.getBrand()).isNotNull();
            assertThat(result.getBrand().getId()).isEqualTo(testBrand.getId());
            assertThat(result.getBrand().getName()).isEqualTo("Test Brand");
        }

        @Test
        @DisplayName("should map empty cars list")
        void toDto_GenerationWithNoCars_MapsEmptyList() {

            // Set up generation with empty cars list
            testGeneration.setCars(List.of());

            // Map valid generation with no cars
            GenerationDetailsResponse result = mapper.toDto(testGeneration);

            // Verify result -> generation with empty cars list is returned
            assertThat(result.getCars()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should map cars list with nested data")
        void toDto_GenerationWithCars_MapsCarsList() {

            // Create test car with engine, transmission and body type
            Engine engine = TestDataFactory.defaultEngine()
                    .id(UUID.randomUUID())
                    .engineType("Petrol")
                    .displacement(2000)
                    .maxPower(200)
                    .build();

            Transmission transmission = TestDataFactory.defaultTransmission()
                    .id(UUID.randomUUID())
                    .transmissionType("Automatic")
                    .build();

            BodyType bodyType = TestDataFactory.defaultBodyType()
                    .id(UUID.randomUUID())
                    .name("Sedan")
                    .build();

            Car car = TestDataFactory.defaultCar(testGeneration, bodyType)
                    .id(UUID.randomUUID())
                    .name("320i")
                    .engine(engine)
                    .transmission(transmission)
                    .images(new LinkedHashSet<>())
                    .tags(new HashSet<>())
                    .build();

            // Add car to generation
            testGeneration.setCars(List.of(car));

            // Map generation containing cars
            GenerationDetailsResponse result = mapper.toDto(testGeneration);

            // Verify result -> correct cars list with engine, transmission and body type data is returned
            assertThat(result.getCars()).hasSize(1);
            assertThat(result.getCars().get(0).getName()).isEqualTo("320i");
            assertThat(result.getCars().get(0).getEngine()).isNotNull();
            assertThat(result.getCars().get(0).getTransmission()).isNotNull();
            assertThat(result.getCars().get(0).getBodyType()).isNotNull();
        }
    }
}

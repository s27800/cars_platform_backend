package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.carImages.CarImage;
import com.carsplatform.backend.api.cars.dtos.CarsListResponse;
import com.carsplatform.backend.api.engines.Engine;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.transmissions.Transmission;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CarsListMapper Integration Tests")
class CarsListMapperTest {

    @Autowired
    private CarsListMapper mapper;

    private Car testCar;
    private Generation testGeneration;

    @BeforeEach
    void setUp() {

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand().id(UUID.randomUUID()).build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand).id(UUID.randomUUID()).build();

        // Create test generation
        testGeneration = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .name("E90")
                .cars(List.of())
                .build();

        // Create test engine
        Engine engine = TestDataFactory.defaultEngine()
                .id(UUID.randomUUID())
                .engineType("Petrol")
                .displacement(2000)
                .maxPower(200)
                .build();

        // Create test transmission
        Transmission transmission = TestDataFactory.defaultTransmission()
                .id(UUID.randomUUID())
                .transmissionType("Automatic")
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .name("Sedan")
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(testGeneration, bodyType)
                .id(UUID.randomUUID())
                .name("320i")
                .engine(engine)
                .transmission(transmission)
                .images(new LinkedHashSet<>())
                .tags(new HashSet<>())
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class toDto {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            CarsListResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map car basic fields correctly")
        void toDto_ValidCar_MapsBasicFields() {

            // Map valid car
            CarsListResponse result = mapper.toDto(testCar);

            // Verify results -> all basic fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testCar.getId());
            assertThat(result.getName()).isEqualTo("320i");
        }

        @Test
        @DisplayName("should map nested engine data")
        void toDto_CarWithEngine_MapsEngineData() {

            // Map valid car with engine
            CarsListResponse result = mapper.toDto(testCar);

            // Verify results -> engine data is mapped correctly
            assertThat(result.getEngine()).isNotNull();
            assertThat(result.getEngine().getEngineType()).isEqualTo("Petrol");
            assertThat(result.getEngine().getDisplacement()).isEqualTo(2000);
            assertThat(result.getEngine().getMaxPower()).isEqualTo(200);
        }

        @Test
        @DisplayName("should map nested transmission data")
        void toDto_CarWithTransmission_MapsTransmissionData() {

            // Map valid car with transmission
            CarsListResponse result = mapper.toDto(testCar);

            // Verify results -> transmission data is mapped correctly
            assertThat(result.getTransmission()).isNotNull();
            assertThat(result.getTransmission().getTransmissionType()).isEqualTo("Automatic");
        }

        @Test
        @DisplayName("should map nested body type data")
        void toDto_CarWithBodyType_MapsBodyTypeData() {

            // Map valid car with body type
            CarsListResponse result = mapper.toDto(testCar);

            // Verify results -> body type data is mapped correctly
            assertThat(result.getBodyType()).isNotNull();
            assertThat(result.getBodyType().getName()).isEqualTo("Sedan");
        }

        @Test
        @DisplayName("should extract main image URL")
        void toDto_CarWithMainImage_ExtractsMainImageUrl() {

            // Create test images
            CarImage mainImage = new CarImage();

            mainImage.setId(UUID.randomUUID());
            mainImage.setImageUrl("http://example.com/main.jpg");
            mainImage.setIsMain(true);
            mainImage.setCar(testCar);

            CarImage secondaryImage = new CarImage();

            secondaryImage.setId(UUID.randomUUID());
            secondaryImage.setImageUrl("http://example.com/secondary.jpg");
            secondaryImage.setIsMain(false);
            secondaryImage.setCar(testCar);

            testCar.setImages(new LinkedHashSet<>(List.of(secondaryImage, mainImage)));

            // Map valid car with images
            CarsListResponse result = mapper.toDto(testCar);

            // Verify results -> main image URL is extracted
            assertThat(result.getImageUrl()).isEqualTo("http://example.com/main.jpg");
        }

        @Test
        @DisplayName("should return null image URL when no images exist")
        void toDto_CarWithNoImages_ReturnsNullImageUrl() {

            // Set no images
            testCar.setImages(new LinkedHashSet<>());

            // Map valid car with no images
            CarsListResponse result = mapper.toDto(testCar);

            // Verify results -> null image URL is returned
            assertThat(result.getImageUrl()).isNull();
        }
    }


    @Nested
    @DisplayName("map")
    class MapTests {

        @Test
        @DisplayName("should map generation to cars list")
        void map_Generation_MapsCarsToList() {

            // Set generation for car
            testCar.setGeneration(testGeneration);
            testGeneration.setCars(List.of(testCar));

            // Map generation to cars list
            List<CarsListResponse> result = mapper.map(testGeneration);

            // Verify results -> cars list is mapped correctly
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("320i");
        }

        @Test
        @DisplayName("should return null when generation is null")
        void map_NullGeneration_ReturnsNull() {

            // Map null generation to cars list
            List<CarsListResponse> result = mapper.map((Generation) null);

            // Verify results -> null is returned
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map page of cars")
        void map_PageOfCars_MapsToPageOfResponses() {

            // Create page of cars
            Page<Car> carsPage = new PageImpl<>(List.of(testCar), PageRequest.of(0, 10), 1);

            // Map page of cars
            Page<CarsListResponse> result = mapper.map(carsPage);

            // Verify results -> page is mapped correctly
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("320i");
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }
}

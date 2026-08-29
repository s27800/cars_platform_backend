package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.carImages.CarImage;
import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.tags.Tag;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("CarDetailsMapper Tests")
class CarDetailsMapperTest {

    @Autowired
    private CarDetailsMapper mapper;

    private Brand testBrand;
    private Model testModel;
    private Generation testGeneration;
    private BodyType testBodyType;
    private Car testCar;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("BMW")
                .build();

        // Create test model
        testModel = TestDataFactory.defaultModel(testBrand)
                .id(UUID.randomUUID())
                .name("3 Series")
                .build();

        // Create test generation
        testGeneration = TestDataFactory.defaultGeneration(testModel)
                .id(UUID.randomUUID())
                .name("G20")
                .build();

        // Create test body type
        testBodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .name("Sedan")
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(testGeneration, testBodyType)
                .id(UUID.randomUUID())
                .name("BMW 320i")
                .description("Compact executive car")
                .doorsNumber(4)
                .seatsNumber(5)
                .productionYears("2019-2023")
                .images(new LinkedHashSet<>())
                .tags(new HashSet<>())
                .build();
    }


    @Nested
    @DisplayName("toDto Tests")
    class toDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            CarDetailsResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map basic car fields correctly")
        void toDto_ValidCar_MapsBasicFields() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> maps all basic fields correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testCar.getId());
            assertThat(result.getName()).isEqualTo("BMW 320i");
            assertThat(result.getDescription()).isEqualTo("Compact executive car");
            assertThat(result.getDoorsNumber()).isEqualTo(4);
            assertThat(result.getSeatsNumber()).isEqualTo(5);
            assertThat(result.getProductionYears()).isEqualTo("2019-2023");
        }

        @Test
        @DisplayName("should map brand from car's generation")
        void toDto_CarWithBrand_MapsBrand() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> brand is mapped correctly
            assertThat(result.getBrand()).isNotNull();
            assertThat(result.getBrand().getName()).isEqualTo("BMW");
        }

        @Test
        @DisplayName("should map model from car's generation")
        void toDto_CarWithModel_MapsModel() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> model is mapped correctly
            assertThat(result.getModel()).isNotNull();
            assertThat(result.getModel().getName()).isEqualTo("3 Series");
        }

        @Test
        @DisplayName("should map generation correctly")
        void toDto_CarWithGeneration_MapsGeneration() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> generation is mapped correctly
            assertThat(result.getGeneration()).isNotNull();
        }

        @Test
        @DisplayName("should map body type correctly")
        void toDto_CarWithBodyType_MapsBodyType() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> body type is mapped correctly
            assertThat(result.getBodyType()).isNotNull();
            assertThat(result.getBodyType().getName()).isEqualTo("Sedan");
        }

        @Test
        @DisplayName("should map engine correctly")
        void toDto_CarWithEngine_MapsEngine() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> engine is mapped correctly
            assertThat(result.getEngine()).isNotNull();
        }

        @Test
        @DisplayName("should map transmission correctly")
        void toDto_CarWithTransmission_MapsTransmission() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> transmission is mapped correctly
            assertThat(result.getTransmission()).isNotNull();
        }

        @Test
        @DisplayName("should map chassis correctly")
        void toDto_CarWithChassis_MapsChassis() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> chassis is mapped correctly
            assertThat(result.getChassis()).isNotNull();
        }

        @Test
        @DisplayName("should map performance correctly")
        void toDto_CarWithPerformance_MapsPerformance() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> performance is mapped correctly
            assertThat(result.getPerformance()).isNotNull();
        }

        @Test
        @DisplayName("should map inside dimensions correctly")
        void toDto_CarWithInsideDimensions_MapsInsideDimensions() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> inside dimensions is mapped correctly
            assertThat(result.getInsideDimensions()).isNotNull();
        }

        @Test
        @DisplayName("should map outside dimensions correctly")
        void toDto_CarWithOutsideDimensions_MapsOutsideDimensions() {

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> outside dimensions is mapped correctly
            assertThat(result.getOutsideDimensions()).isNotNull();
        }

        @Test
        @DisplayName("should map empty images list")
        void toDto_CarWithEmptyImages_MapsEmptyList() {

            // Set empty images list for car
            testCar.setImages(new LinkedHashSet<>());

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> empty images list is mapped correctly
            assertThat(result.getImages()).isEmpty();
        }

        @Test
        @DisplayName("should map images when present")
        void toDto_CarWithImages_MapsImages() {

            // Create test image
            CarImage image = CarImage.builder()
                    .id(UUID.randomUUID())
                    .imageUrl("http://example.com/image.jpg")
                    .car(testCar)
                    .build();

            testCar.setImages(new LinkedHashSet<>(Set.of(image)));

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> images are mapped correctly
            assertThat(result.getImages()).hasSize(1);
        }

        @Test
        @DisplayName("should map empty tags set")
        void toDto_CarWithEmptyTags_MapsEmptySet() {

            // Set empty tags for car
            testCar.setTags(new HashSet<>());

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> empty tags set is mapped correctly
            assertThat(result.getTags()).isEmpty();
        }

        @Test
        @DisplayName("should map tags when present")
        void toDto_CarWithTags_MapsTags() {

            // Create test tag
            Tag tag = TestDataFactory.defaultTag()
                    .id(UUID.randomUUID())
                    .name("Electric")
                    .build();

            testCar.setTags(Set.of(tag));

            // Map valid car
            CarDetailsResponse result = mapper.toDto(testCar);

            // Verify result -> tags are mapped correctly
            assertThat(result.getTags()).hasSize(1);
        }
    }
}

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
        Brand brand = TestDataFactory.defaultBrand().id(UUID.randomUUID()).build();
        Model model = TestDataFactory.defaultModel(brand).id(UUID.randomUUID()).build();
        testGeneration = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .name("E90")
                .cars(List.of())
                .build();
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
            CarsListResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map car basic fields correctly")
        void toDto_ValidCar_MapsBasicFields() {
            CarsListResponse result = mapper.toDto(testCar);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testCar.getId());
            assertThat(result.getName()).isEqualTo("320i");
        }

        @Test
        @DisplayName("should map nested engine data")
        void toDto_CarWithEngine_MapsEngineData() {
            CarsListResponse result = mapper.toDto(testCar);
            assertThat(result.getEngine()).isNotNull();
            assertThat(result.getEngine().getEngineType()).isEqualTo("Petrol");
            assertThat(result.getEngine().getDisplacement()).isEqualTo(2000);
            assertThat(result.getEngine().getMaxPower()).isEqualTo(200);
        }

        @Test
        @DisplayName("should map nested transmission data")
        void toDto_CarWithTransmission_MapsTransmissionData() {
            CarsListResponse result = mapper.toDto(testCar);
            assertThat(result.getTransmission()).isNotNull();
            assertThat(result.getTransmission().getTransmissionType()).isEqualTo("Automatic");
        }

        @Test
        @DisplayName("should map nested body type data")
        void toDto_CarWithBodyType_MapsBodyTypeData() {
            CarsListResponse result = mapper.toDto(testCar);
            assertThat(result.getBodyType()).isNotNull();
            assertThat(result.getBodyType().getName()).isEqualTo("Sedan");
        }

        @Test
        @DisplayName("should extract main image URL")
        void toDto_CarWithMainImage_ExtractsMainImageUrl() {
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

            CarsListResponse result = mapper.toDto(testCar);
            assertThat(result.getImageUrl()).isEqualTo("http://example.com/main.jpg");
        }

        @Test
        @DisplayName("should return null image URL when no images exist")
        void toDto_CarWithNoImages_ReturnsNullImageUrl() {
            testCar.setImages(new LinkedHashSet<>());

            CarsListResponse result = mapper.toDto(testCar);
            assertThat(result.getImageUrl()).isNull();
        }
    }


    @Nested
    @DisplayName("map")
    class MapTests {

        @Test
        @DisplayName("should map generation to cars list")
        void map_Generation_MapsCarsToList() {
            testCar.setGeneration(testGeneration);
            testGeneration.setCars(List.of(testCar));

            List<CarsListResponse> result = mapper.map(testGeneration);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("320i");
        }

        @Test
        @DisplayName("should return null when generation is null")
        void map_NullGeneration_ReturnsNull() {
            List<CarsListResponse> result = mapper.map((Generation) null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map page of cars")
        void map_PageOfCars_MapsToPageOfResponses() {
            Page<Car> carsPage = new PageImpl<>(List.of(testCar), PageRequest.of(0, 10), 1);

            Page<CarsListResponse> result = mapper.map(carsPage);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("320i");
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }
}

package com.carsplatform.backend.api.carImages;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.carImages.dtos.CarImageResponse;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DisplayName("CarImageMapper Tests")
class CarImageMapperTest {

    private final CarImageMapper mapper = Mappers.getMapper(CarImageMapper.class);

    private Car testCar;
    private CarImage testImage;

    @BeforeEach
    void setUp() {

        // Create test car and image
        Brand brand = TestDataFactory.defaultBrand().id(1).build();
        Model model = TestDataFactory.defaultModel(brand).id(1).build();
        Generation generation = TestDataFactory.defaultGeneration(model).id(1).build();
        BodyType bodyType = TestDataFactory.defaultBodyType().id(1).build();

        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(1)
                .images(new ArrayList<>())
                .build();

        testImage = CarImage.builder()
                .id(1)
                .imageUrl("https://example.com/car-image.jpg")
                .isMain(true)
                .car(testCar)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            CarImageResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidCarImage_MapsAllFields() {

            // Map valid car image
            CarImageResponse result = mapper.toDto(testImage);

            // Verify all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getImageUrl()).isEqualTo("https://example.com/car-image.jpg");
            assertThat(result.getIsMain()).isTrue();
        }

        @Test
        @DisplayName("should map non-main image correctly")
        void toDto_NonMainImage_MapsIsMainFalse() {

            // Set image as non-main
            testImage.setIsMain(false);

            // Map non-main image
            CarImageResponse result = mapper.toDto(testImage);

            // Verify isMain is false
            assertThat(result.getIsMain()).isFalse();
        }
    }


    @Nested
    @DisplayName("toDtoList")
    class ToDtoListTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDtoList_NullInput_ReturnsNull() {

            // Map null input
            List<CarImageResponse> result = mapper.toDtoList(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return empty list when input is empty")
        void toDtoList_EmptyList_ReturnsEmptyList() {

            // Map empty list
            List<CarImageResponse> result = mapper.toDtoList(new ArrayList<>());

            // Verify result is empty
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map all images correctly")
        void toDtoList_ListWithImages_MapsAllImages() {

            // Create list of car images
            CarImage image1 = CarImage.builder()
                    .id(1)
                    .imageUrl("https://example.com/image1.jpg")
                    .isMain(true)
                    .car(testCar)
                    .build();

            CarImage image2 = CarImage.builder()
                    .id(2)
                    .imageUrl("https://example.com/image2.jpg")
                    .isMain(false)
                    .car(testCar)
                    .build();

            List<CarImage> images = List.of(image1, image2);

            // Map list of car images
            List<CarImageResponse> result = mapper.toDtoList(images);

            // Verify all car images are mapped correctly
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getImageUrl()).isEqualTo("https://example.com/image1.jpg");
            assertThat(result.get(0).getIsMain()).isTrue();
            assertThat(result.get(1).getImageUrl()).isEqualTo("https://example.com/image2.jpg");
            assertThat(result.get(1).getIsMain()).isFalse();
        }
    }
}

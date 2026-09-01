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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("CarImageMapper Tests")
class CarImageMapperTest {

    private final CarImageMapper mapper = Mappers.getMapper(CarImageMapper.class);

    private Car testCar;
    private CarImage testImage;

    @BeforeEach
    void setUp() {
        Brand brand = TestDataFactory.defaultBrand().id(UUID.randomUUID()).build();
        Model model = TestDataFactory.defaultModel(brand).id(UUID.randomUUID()).build();
        Generation generation = TestDataFactory.defaultGeneration(model).id(UUID.randomUUID()).build();
        BodyType bodyType = TestDataFactory.defaultBodyType().id(UUID.randomUUID()).build();

        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .images(new LinkedHashSet<>())
                .build();

        testImage = CarImage.builder()
                .id(UUID.randomUUID())
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
            CarImageResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidCarImage_MapsAllFields() {
            CarImageResponse result = mapper.toDto(testImage);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testImage.getId());
            assertThat(result.getImageUrl()).isEqualTo("https://example.com/car-image.jpg");
            assertThat(result.getIsMain()).isTrue();
        }

        @Test
        @DisplayName("should map non-main image correctly")
        void toDto_NonMainImage_MapsIsMainFalse() {
            testImage.setIsMain(false);

            CarImageResponse result = mapper.toDto(testImage);

            assertThat(result.getIsMain()).isFalse();
        }
    }


    @Nested
    @DisplayName("toDtoList")
    class ToDtoListTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDtoList_NullInput_ReturnsNull() {
            List<CarImageResponse> result = mapper.toDtoList(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return empty list when input is empty")
        void toDtoList_EmptyList_ReturnsEmptyList() {
            List<CarImageResponse> result = mapper.toDtoList(new LinkedHashSet<>());
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map all images correctly")
        void toDtoList_ListWithImages_MapsAllImages() {
            CarImage image1 = CarImage.builder()
                    .id(UUID.randomUUID())
                    .imageUrl("https://example.com/image1.jpg")
                    .isMain(true)
                    .car(testCar)
                    .build();

            CarImage image2 = CarImage.builder()
                    .id(UUID.randomUUID())
                    .imageUrl("https://example.com/image2.jpg")
                    .isMain(false)
                    .car(testCar)
                    .build();

            List<CarImage> images = List.of(image1, image2);

            List<CarImageResponse> result = mapper.toDtoList(images);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getImageUrl()).isEqualTo("https://example.com/image1.jpg");
            assertThat(result.get(0).getIsMain()).isTrue();
            assertThat(result.get(1).getImageUrl()).isEqualTo("https://example.com/image2.jpg");
            assertThat(result.get(1).getIsMain()).isFalse();
        }
    }
}

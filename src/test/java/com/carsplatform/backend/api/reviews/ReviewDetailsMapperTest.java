package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.dtos.ReviewDetailsResponse;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ReviewDetailsMapper Tests")
class ReviewDetailsMapperTest {

    @Autowired
    private ReviewDetailsMapper reviewDetailsMapper;

    private User testUser;
    private Car testCar;
    private Review testReview;
    private Brand brand;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();
        brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("BMW")
                .build();
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .name("3 Series")
                .build();
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .name("E90")
                .build();
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .name("320i")
                .build();
        testReview = TestDataFactory.defaultReview(testUser, testCar)
                .id(UUID.randomUUID())
                .status(ModerationStatus.PENDING)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should map review to ReviewDetailsResponse")
        void toDto_ValidReview_ReturnsReviewDetailsResponse() {
            ReviewDetailsResponse result = reviewDetailsMapper.toDto(testReview);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testReview.getId());
            assertThat(result.getComment()).isEqualTo(testReview.getComment());
            assertThat(result.getEngineRating()).isEqualTo(testReview.getEngineRating());
            assertThat(result.getTransmissionRating()).isEqualTo(testReview.getTransmissionRating());
            assertThat(result.getStatus()).isEqualTo(testReview.getStatus());
            assertThat(result.getUsernameResponse()).isNotNull();
            assertThat(result.getUsernameResponse().getUsername()).isEqualTo(testUser.getUsername());
        }

        @Test
        @DisplayName("should map car info correctly")
        void toDto_ValidReview_MapsCarInfoCorrectly() {
            ReviewDetailsResponse result = reviewDetailsMapper.toDto(testReview);

            assertThat(result.getCarInfo()).isNotNull();
            assertThat(result.getCarInfo().getCarId()).isEqualTo(testCar.getId());
            assertThat(result.getCarInfo().getCarName()).isEqualTo(testCar.getName());
            assertThat(result.getCarInfo().getBrandName()).isEqualTo("BMW");
            assertThat(result.getCarInfo().getModelName()).isEqualTo("3 Series");
            assertThat(result.getCarInfo().getGenerationName()).isEqualTo("E90");
        }

        @Test
        @DisplayName("should return null when review is null")
        void toDto_NullReview_ReturnsNull() {
            ReviewDetailsResponse result = reviewDetailsMapper.toDto(null);
            assertThat(result).isNull();
        }
    }


    @Nested
    @DisplayName("toDtoList")
    class ToDtoListTests {

        @Test
        @DisplayName("should map page of reviews to page of ReviewDetailsResponse")
        void toDtoList_ValidPage_ReturnsMappedPage() {
            Page<Review> reviewPage = new PageImpl<>(List.of(testReview));

            Page<ReviewDetailsResponse> result = reviewDetailsMapper.toDtoList(reviewPage);
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(testReview.getId());
        }

        @Test
        @DisplayName("should return null when page is null")
        void toDtoList_NullPage_ReturnsNull() {
            Page<ReviewDetailsResponse> result = reviewDetailsMapper.toDtoList(null);
            assertThat(result).isNull();
        }
    }
}

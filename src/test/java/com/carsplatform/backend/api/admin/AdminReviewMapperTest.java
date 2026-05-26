package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.admin.dtos.AdminCarInfoResponse;
import com.carsplatform.backend.api.admin.dtos.AdminReviewResponse;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.users.User;
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

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AdminReviewMapper Tests")
class AdminReviewMapperTest {

    @Autowired
    private AdminReviewMapper adminReviewMapper;

    private User testUser;
    private Car testCar;
    private Review testReview;
    private Brand brand;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(1L)
                .build();

        // Create test brand
        brand = TestDataFactory.defaultBrand()
                .id(1)
                .name("BMW")
                .build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .id(1)
                .name("3 Series")
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(1)
                .name("E90")
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(1)
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(1)
                .name("320i")
                .build();

        // Create test review
        testReview = TestDataFactory.defaultReview(testUser, testCar)
                .id(1L)
                .isApproved(false)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should map review to AdminReviewResponse")
        void toDto_ValidReview_ReturnsAdminReviewResponse() {

            // Map review to DTO
            AdminReviewResponse result = adminReviewMapper.toDto(testReview);

            // Verify results -> all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testReview.getId());
            assertThat(result.getComment()).isEqualTo(testReview.getComment());
            assertThat(result.getEngineRating()).isEqualTo(testReview.getEngineRating());
            assertThat(result.getTransmissionRating()).isEqualTo(testReview.getTransmissionRating());
            assertThat(result.getIsApproved()).isEqualTo(testReview.getIsApproved());
            assertThat(result.getUsernameResponse()).isNotNull();
            assertThat(result.getUsernameResponse().getUsername()).isEqualTo(testUser.getUsername());
        }

        @Test
        @DisplayName("should map car info correctly")
        void toDto_ValidReview_MapsCarInfoCorrectly() {

            // Map review to DTO
            AdminReviewResponse result = adminReviewMapper.toDto(testReview);

            // Verify car info is mapped correctly
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

            // Map null review to DTO
            AdminReviewResponse result = adminReviewMapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }
    }


    @Nested
    @DisplayName("toCarInfo")
    class ToCarInfoTests {

        @Test
        @DisplayName("should map car to AdminCarInfoResponse")
        void toCarInfo_ValidCar_ReturnsAdminCarInfoResponse() {

            // Map car to DTO
            AdminCarInfoResponse result = adminReviewMapper.toCarInfo(testCar);

            // Verify results -> all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getCarId()).isEqualTo(testCar.getId());
            assertThat(result.getCarName()).isEqualTo(testCar.getName());
            assertThat(result.getBrandName()).isEqualTo("BMW");
            assertThat(result.getModelName()).isEqualTo("3 Series");
            assertThat(result.getGenerationName()).isEqualTo("E90");
        }

        @Test
        @DisplayName("should return null when car is null")
        void toCarInfo_NullCar_ReturnsNull() {

            // Map null car to DTO
            AdminCarInfoResponse result = adminReviewMapper.toCarInfo(null);

            // Verify result is null
            assertThat(result).isNull();
        }
    }


    @Nested
    @DisplayName("toDtoList")
    class ToDtoListTests {

        @Test
        @DisplayName("should map page of reviews to page of AdminReviewResponse")
        void toDtoList_ValidPage_ReturnsMappedPage() {

            // Create page of reviews
            Page<Review> reviewPage = new PageImpl<>(List.of(testReview));

            // Map page to DTOs
            Page<AdminReviewResponse> result = adminReviewMapper.toDtoList(reviewPage);

            // Verify results -> page is mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(testReview.getId());
        }

        @Test
        @DisplayName("should return null when page is null")
        void toDtoList_NullPage_ReturnsNull() {

            // Map null page to DTOs
            Page<AdminReviewResponse> result = adminReviewMapper.toDtoList(null);

            // Verify result is null
            assertThat(result).isNull();
        }
    }
}

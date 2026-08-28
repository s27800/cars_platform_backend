package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.dtos.ReviewResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ReviewMapper Tests")
class ReviewMapperTest {

    @Autowired
    private ReviewMapper mapper;

    private User testUser;
    private Car testCar;
    private Review testReview;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .username("reviewer")
                .build();

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .build();

        // Create test review
        testReview = TestDataFactory.defaultReview(testUser, testCar)
                .id(UUID.randomUUID())
                .comment("Great car!")
                .engineRating(5)
                .transmissionRating(4)
                .steeringRating(4)
                .suspensionRating(3)
                .visibilityRating(4)
                .ergonomicsRating(5)
                .soundProofingRating(3)
                .interiorSpaceRating(4)
                .maintenanceRating(3)
                .priceQualityRating(4)
                .failureFreeRating(4)
                .reviewDate(LocalDateTime.of(2024, 1, 15, 10, 30))
                .isApproved(true)
                .likesCount(5L)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            ReviewResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidReview_MapsAllFields() {

            // Map valid review
            ReviewResponse result = mapper.toDto(testReview);

            // Verify results -> fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testReview.getId());
            assertThat(result.getComment()).isEqualTo("Great car!");
            assertThat(result.getEngineRating()).isEqualTo(5.0);
            assertThat(result.getTransmissionRating()).isEqualTo(4.0);
            assertThat(result.getSteeringRating()).isEqualTo(4.0);
            assertThat(result.getSuspensionRating()).isEqualTo(3.0);
            assertThat(result.getVisibilityRating()).isEqualTo(4.0);
            assertThat(result.getErgonomicsRating()).isEqualTo(5.0);
            assertThat(result.getSoundProofingRating()).isEqualTo(3.0);
            assertThat(result.getInteriorSpaceRating()).isEqualTo(4.0);
            assertThat(result.getMaintenanceRating()).isEqualTo(3.0);
            assertThat(result.getPriceQualityRating()).isEqualTo(4.0);
            assertThat(result.getFailureFreeRating()).isEqualTo(4.0);
            assertThat(result.getReviewDate()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
            assertThat(result.getIsApproved()).isTrue();
            assertThat(result.getLikesCount()).isEqualTo(5L);
        }

        @Test
        @DisplayName("should map user to usernameResponse")
        void toDto_ReviewWithUser_MapsUsernameResponse() {

            // Map valid review
            ReviewResponse result = mapper.toDto(testReview);

            // Verify results -> user is mapped to correctly
            assertThat(result.getUsernameResponse()).isNotNull();
            assertThat(result.getUsernameResponse().getUsername()).isEqualTo("reviewer");
        }

        @Test
        @DisplayName("should handle review with null user")
        void toDto_ReviewWithNullUser_HandlesGracefully() {

            // Set user to null on test review
            testReview.setUser(null);

            // Map review with null user
            ReviewResponse result = mapper.toDto(testReview);

            // Verify results -> correct null user handling
            assertThat(result).isNotNull();
            assertThat(result.getUsernameResponse()).isNull();
        }

        @Test
        @DisplayName("should handle zero likes count")
        void toDto_ReviewWithZeroLikes_MapsZero() {

            // Set likes count to zero
            testReview.setLikesCount(0L);

            // Map review with zero likes
            ReviewResponse result = mapper.toDto(testReview);

            // Verify results -> correct zero likes mapping
            assertThat(result.getLikesCount()).isEqualTo(0L);
        }
    }


    @Nested
    @DisplayName("toDtoList")
    class ToDtoListTests {

        @Test
        @DisplayName("should return null when page is null")
        void toDtoList_NullPage_ReturnsNull() {

            // Map null page
            Page<ReviewResponse> result = mapper.toDtoList(null);

            // Verify results -> correct null handling
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map empty page")
        void toDtoList_EmptyPage_ReturnsEmptyPage() {

            // Create empty page
            Page<Review> emptyPage = Page.empty();

            // Map empty page
            Page<ReviewResponse> result = mapper.toDtoList(emptyPage);

            // Verify results -> correct empty page mapping
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map page with reviews")
        void toDtoList_PageWithReviews_MapsAllReviews() {

            // Create page with multiple reviews
            Review review1 = TestDataFactory.defaultReview(testUser, testCar)
                    .id(UUID.randomUUID())
                    .comment("Review 1")
                    .build();

            Review review2 = TestDataFactory.defaultReview(testUser, testCar)
                    .id(UUID.randomUUID())
                    .comment("Review 2")
                    .build();

            Page<Review> reviewPage = new PageImpl<>(
                    List.of(review1, review2),
                    PageRequest.of(0, 10),
                    2
            );

            // Map page with multiple reviews
            Page<ReviewResponse> result = mapper.toDtoList(reviewPage);

            // Verify results -> correct mapping of all reviews
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getComment()).isEqualTo("Review 1");
            assertThat(result.getContent().get(1).getComment()).isEqualTo("Review 2");
        }

        @Test
        @DisplayName("should preserve pagination information")
        void toDtoList_PageWithPagination_PreservesPaginationInfo() {

            // Create page with specific pagination
            Review review = TestDataFactory.defaultReview(testUser, testCar)
                    .id(UUID.randomUUID())
                    .build();

            Page<Review> reviewPage = new PageImpl<>(
                    List.of(review),
                    PageRequest.of(2, 5),
                    100
            );

            // Map page with specific pagination
            Page<ReviewResponse> result = mapper.toDtoList(reviewPage);

            // Verify results -> correct pagination preservation
            assertThat(result.getNumber()).isEqualTo(2);
            assertThat(result.getSize()).isEqualTo(5);
            assertThat(result.getTotalElements()).isEqualTo(100);
            assertThat(result.getTotalPages()).isEqualTo(20);
        }
    }
}

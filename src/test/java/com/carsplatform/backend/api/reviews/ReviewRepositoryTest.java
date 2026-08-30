package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ReviewRepository Integration Tests")
class ReviewRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private User testUser;
    private Car testCar;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .username("reviewer")
                .email("reviewer@example.com")
                .build();

        entityManager.persist(testUser);

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .name("BMW")
                .build();

        entityManager.persist(brand);

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .name("3 Series")
                .build();

        entityManager.persist(model);

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .name("E90")
                .build();

        entityManager.persist(generation);

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();

        entityManager.persist(bodyType);

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .name("320i")
                .build();

        entityManager.persist(testCar.getEngine());
        entityManager.persist(testCar.getTransmission());
        entityManager.persist(testCar.getChassis());
        entityManager.persist(testCar.getPerformance());
        entityManager.persist(testCar.getInsideDimensions());
        entityManager.persist(testCar.getOutsideDimensions());
        entityManager.persist(testCar);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("findAllApprovedByCarId")
    class FindAllApprovedByCarIdTests {

        @Test
        @DisplayName("returns only approved reviews")
        void approvedReviews_ReturnsOnlyApproved() {

            // Create second user for pending review
            User user2 = TestDataFactory.createUser("pending");
            entityManager.persist(user2);

            // Create approved and pending reviews for the same car
            Review approvedReview = createReview(testUser, testCar, true);
            Review pendingReview = createReview(user2, testCar, false);

            entityManager.persist(approvedReview);
            entityManager.persist(pendingReview);

            entityManager.flush();

            // find all approved reviews for the car
            Page<Review> result = reviewRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 10));

            // Verify results -> only approved review is returned
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getIsApproved()).isTrue();
        }

        @Test
        @DisplayName("returns empty for car with no approved reviews")
        void noApprovedReviews_ReturnsEmpty() {

            // Create pending review for the car
            Review pendingReview = createReview(testUser, testCar, false);

            entityManager.persist(pendingReview);
            entityManager.flush();

            // find all approved reviews for the car
            Page<Review> result = reviewRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 10));

            // Verify results -> no approved reviews
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("returns paginated results")
        void multipleReviews_ReturnsPaginated() {

            // Create multiple approved reviews for the car
            for (int i = 0; i < 5; i++) {
                User user = TestDataFactory.createUser(String.valueOf(i));
                entityManager.persist(user);

                Review review = createReview(user, testCar, true);
                entityManager.persist(review);
            }

            entityManager.flush();

            // find all approved reviews for the car with pagination
            Page<Review> result = reviewRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 3));

            // Verify results -> paginated reviews are returned
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("fetches user eagerly")
        void withUser_FetchesUserEagerly() {

            // Create approved review for the car
            Review review = createReview(testUser, testCar, true);

            entityManager.persist(review);
            entityManager.flush();
            entityManager.clear();

            // find all approved reviews for the car
            Page<Review> result = reviewRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 10));

            // Verify results -> user is fetched eagerly
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUser()).isNotNull();
            assertThat(result.getContent().get(0).getUser().getUsername()).isEqualTo("reviewer");
        }
    }


    @Nested
    @DisplayName("findAverageRatingsForCarId")
    class FindAverageRatingsForCarIdTests {

        @Test
        @DisplayName("calculates averages correctly")
        void multipleReviews_CalculatesAverages() {

            // Create multiple approved reviews for the car
            Review review1 = createReviewWithRatings(testUser, testCar, 4, true);

            User user2 = TestDataFactory.createUser("2");
            entityManager.persist(user2);

            Review review2 = createReviewWithRatings(user2, testCar, 2, true);

            entityManager.persist(review1);
            entityManager.persist(review2);
            entityManager.flush();

            // Find average ratings for the car
            AverageRatingsResponse result = reviewRepository.findAverageRatingsForCarId(testCar.getId());

            // Verify results -> averages are calculated correctly
            assertThat(result).isNotNull();
            assertThat(result.getAvgEngineRating()).isEqualTo(3.0);
            assertThat(result.getAvgTransmissionRating()).isEqualTo(3.0);
        }

        @Test
        @DisplayName("ignores non-approved reviews")
        void withPendingReviews_IgnoresPending() {

            // Create approved and pending reviews for the car
            Review approvedReview = createReviewWithRatings(testUser, testCar, 5, true);

            User user2 = TestDataFactory.createUser("2");
            entityManager.persist(user2);

            Review pendingReview = createReviewWithRatings(user2, testCar, 1, false);

            entityManager.persist(approvedReview);
            entityManager.persist(pendingReview);
            entityManager.flush();

            // Find average ratings for the car
            AverageRatingsResponse result = reviewRepository.findAverageRatingsForCarId(testCar.getId());

            // Verify results -> averages are calculated correctly
            assertThat(result).isNotNull();
            assertThat(result.getAvgEngineRating()).isEqualTo(5.0);
        }
    }


    @Nested
    @DisplayName("existsByCarIdAndUserId")
    class ExistsByCarIdAndUserIdTests {

        @Test
        @DisplayName("returns true when review exists")
        void reviewExists_ReturnsTrue() {

            // Create approved review for the car
            Review review = createReview(testUser, testCar, false);

            entityManager.persist(review);
            entityManager.flush();

            // Check if review exists
            boolean exists = reviewRepository.existsByCarIdAndUserId(testCar.getId(), testUser.getId());

            // Verify results -> review exists
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("returns false when review does not exist")
        void noReview_ReturnsFalse() {

            // Check if review exists
            boolean exists = reviewRepository.existsByCarIdAndUserId(testCar.getId(), testUser.getId());

            // Verify results -> review does not exist
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("returns false for different user")
        void differentUser_ReturnsFalse() {

            // Create approved review for the car
            Review review = createReview(testUser, testCar, true);

            entityManager.persist(review);
            entityManager.flush();

            User otherUser = TestDataFactory.createUser("other");

            entityManager.persist(otherUser);
            entityManager.flush();

            // Check if review exists
            boolean exists = reviewRepository.existsByCarIdAndUserId(testCar.getId(), otherUser.getId());

            // Verify results -> review does not exist
            assertThat(exists).isFalse();
        }
    }


    // helper methods

    private Review createReview(User user, Car car, boolean isApproved) {
        return Review.builder()
                .user(user)
                .car(car)
                .comment("Test review comment")
                .engineRating(4)
                .transmissionRating(4)
                .steeringRating(4)
                .suspensionRating(4)
                .visibilityRating(4)
                .ergonomicsRating(4)
                .soundProofingRating(4)
                .interiorSpaceRating(4)
                .maintenanceRating(4)
                .priceQualityRating(4)
                .failureFreeRating(4)
                .isApproved(isApproved)
                .reviewDate(LocalDateTime.now())
                .build();
    }

    private Review createReviewWithRatings(User user, Car car, int rating, boolean isApproved) {
        return Review.builder()
                .user(user)
                .car(car)
                .comment("Test review")
                .engineRating(rating)
                .transmissionRating(rating)
                .steeringRating(rating)
                .suspensionRating(rating)
                .visibilityRating(rating)
                .ergonomicsRating(rating)
                .soundProofingRating(rating)
                .interiorSpaceRating(rating)
                .maintenanceRating(rating)
                .priceQualityRating(rating)
                .failureFreeRating(rating)
                .isApproved(isApproved)
                .reviewDate(LocalDateTime.now())
                .build();
    }
}

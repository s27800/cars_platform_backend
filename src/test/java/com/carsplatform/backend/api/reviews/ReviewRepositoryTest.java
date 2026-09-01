package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;
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
        testUser = TestDataFactory.defaultUser()
                .username("reviewer")
                .email("reviewer@example.com")
                .build();

        entityManager.persist(testUser);
        Brand brand = TestDataFactory.defaultBrand()
                .name("BMW")
                .build();

        entityManager.persist(brand);
        Model model = TestDataFactory.defaultModel(brand)
                .name("3 Series")
                .build();

        entityManager.persist(model);
        Generation generation = TestDataFactory.defaultGeneration(model)
                .name("E90")
                .build();

        entityManager.persist(generation);
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();

        entityManager.persist(bodyType);
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

        entityManager.flush();
    }


    @Nested
    @DisplayName("findAllApprovedByCarId")
    class FindAllApprovedByCarIdTests {

        @Test
        @DisplayName("returns only approved reviews")
        void approvedReviews_ReturnsOnlyApproved() {
            User user2 = TestDataFactory.createUser("pending");
            entityManager.persist(user2);

            Review approvedReview = createReview(testUser, testCar, ModerationStatus.APPROVED);
            Review pendingReview = createReview(user2, testCar, ModerationStatus.PENDING);

            entityManager.persist(approvedReview);
            entityManager.persist(pendingReview);

            entityManager.flush();

            Page<Review> result = reviewRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getStatus()).isEqualTo(ModerationStatus.APPROVED);
        }

        @Test
        @DisplayName("returns empty for car with no approved reviews")
        void noApprovedReviews_ReturnsEmpty() {
            Review pendingReview = createReview(testUser, testCar, ModerationStatus.PENDING);

            entityManager.persist(pendingReview);
            entityManager.flush();

            Page<Review> result = reviewRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 10));
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("returns paginated results")
        void multipleReviews_ReturnsPaginated() {
            for (int i = 0; i < 5; i++) {
                User user = TestDataFactory.createUser(String.valueOf(i));
                entityManager.persist(user);

                Review review = createReview(user, testCar, ModerationStatus.APPROVED);
                entityManager.persist(review);
            }

            entityManager.flush();

            Page<Review> result = reviewRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 3));
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("fetches user eagerly")
        void withUser_FetchesUserEagerly() {
            Review review = createReview(testUser, testCar, ModerationStatus.APPROVED);

            entityManager.persist(review);
            entityManager.flush();
            entityManager.clear();

            Page<Review> result = reviewRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 10));
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
            Review review1 = createReviewWithRatings(testUser, testCar, 4, ModerationStatus.APPROVED);

            User user2 = TestDataFactory.createUser("2");
            entityManager.persist(user2);

            Review review2 = createReviewWithRatings(user2, testCar, 2, ModerationStatus.APPROVED);

            entityManager.persist(review1);
            entityManager.persist(review2);
            entityManager.flush();

            AverageRatingsResponse result = reviewRepository.findAverageRatingsForCarId(testCar.getId());
            assertThat(result).isNotNull();
            assertThat(result.getAvgEngineRating()).isEqualTo(3.0);
            assertThat(result.getAvgTransmissionRating()).isEqualTo(3.0);
        }

        @Test
        @DisplayName("ignores non-approved reviews")
        void withPendingReviews_IgnoresPending() {
            Review approvedReview = createReviewWithRatings(testUser, testCar, 5, ModerationStatus.APPROVED);

            User user2 = TestDataFactory.createUser("2");
            entityManager.persist(user2);

            Review pendingReview = createReviewWithRatings(user2, testCar, 1, ModerationStatus.PENDING);

            entityManager.persist(approvedReview);
            entityManager.persist(pendingReview);
            entityManager.flush();

            AverageRatingsResponse result = reviewRepository.findAverageRatingsForCarId(testCar.getId());
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
            Review review = createReview(testUser, testCar, ModerationStatus.PENDING);

            entityManager.persist(review);
            entityManager.flush();

            boolean exists = reviewRepository.existsByCarIdAndUserId(testCar.getId(), testUser.getId());
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("returns false when review does not exist")
        void noReview_ReturnsFalse() {
            boolean exists = reviewRepository.existsByCarIdAndUserId(testCar.getId(), testUser.getId());
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("returns false for different user")
        void differentUser_ReturnsFalse() {
            Review review = createReview(testUser, testCar, ModerationStatus.APPROVED);

            entityManager.persist(review);
            entityManager.flush();

            User otherUser = TestDataFactory.createUser("other");

            entityManager.persist(otherUser);
            entityManager.flush();

            boolean exists = reviewRepository.existsByCarIdAndUserId(testCar.getId(), otherUser.getId());
            assertThat(exists).isFalse();
        }
    }


    private Review createReview(User user, Car car, ModerationStatus status) {
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
                .status(status)
                .reviewDate(LocalDateTime.now())
                .build();
    }

    private Review createReviewWithRatings(User user, Car car, int rating, ModerationStatus status) {
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
                .status(status)
                .reviewDate(LocalDateTime.now())
                .build();
    }
}

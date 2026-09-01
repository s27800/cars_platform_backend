package com.carsplatform.backend.api.reviewLikes;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ReviewLikeRepository Integration Tests")
class ReviewLikeRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    private User testUser;
    private User anotherUser;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.defaultUser()
                .username("liker")
                .email("liker@example.com")
                .build();
        entityManager.persist(testUser);

        anotherUser = TestDataFactory.defaultUser()
                .username("reviewer")
                .email("reviewer@example.com")
                .build();
        entityManager.persist(anotherUser);

        Brand brand = TestDataFactory.defaultBrand().name("BMW").build();
        entityManager.persist(brand);

        Model model = TestDataFactory.defaultModel(brand).build();
        entityManager.persist(model);

        Generation generation = TestDataFactory.defaultGeneration(model).build();
        entityManager.persist(generation);

        BodyType bodyType = TestDataFactory.defaultBodyType().build();
        entityManager.persist(bodyType);

        Car car = TestDataFactory.defaultCar(generation, bodyType).build();
        entityManager.persist(car.getEngine());
        entityManager.persist(car.getTransmission());
        entityManager.persist(car.getChassis());
        entityManager.persist(car.getPerformance());
        entityManager.persist(car.getInsideDimensions());
        entityManager.persist(car.getOutsideDimensions());
        entityManager.persist(car);

        testReview = Review.builder()
                .user(anotherUser)
                .car(car)
                .comment("Great car!")
                .engineRating(5)
                .transmissionRating(5)
                .steeringRating(5)
                .suspensionRating(5)
                .visibilityRating(5)
                .ergonomicsRating(5)
                .soundProofingRating(5)
                .interiorSpaceRating(5)
                .maintenanceRating(5)
                .priceQualityRating(5)
                .failureFreeRating(5)
                .status(ModerationStatus.APPROVED)
                .reviewDate(LocalDateTime.now())
                .build();
        entityManager.persist(testReview);

        entityManager.flush();
    }


    @Nested
    @DisplayName("findByUserIdAndReviewId Tests")
    class FindByUserIdAndReviewIdTests {

        @Test
        @DisplayName("returns like when exists")
        void findByUserIdAndReviewId_LikeExists_ReturnsLike() {
            ReviewLike like = TestDataFactory.defaultReviewLike(testUser, testReview).build();
            entityManager.persist(like);
            entityManager.flush();

            Optional<ReviewLike> result = reviewLikeRepository.findByUserIdAndReviewId(
                    testUser.getId(), testReview.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
            assertThat(result.get().getReview().getId()).isEqualTo(testReview.getId());
        }

        @Test
        @DisplayName("returns empty when like does not exist")
        void findByUserIdAndReviewId_NoLike_ReturnsEmpty() {
            Optional<ReviewLike> result = reviewLikeRepository.findByUserIdAndReviewId(
                    testUser.getId(), testReview.getId());
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty for different user")
        void findByUserIdAndReviewId_DifferentUser_ReturnsEmpty() {
            ReviewLike like = TestDataFactory.defaultReviewLike(testUser, testReview).build();
            entityManager.persist(like);
            entityManager.flush();

            Optional<ReviewLike> result = reviewLikeRepository.findByUserIdAndReviewId(
                    anotherUser.getId(), testReview.getId());
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("existsByUserIdAndReviewId Tests")
    class ExistsByUserIdAndReviewIdTests {

        @Test
        @DisplayName("returns true when like exists")
        void existsByUserIdAndReviewId_LikeExists_ReturnsTrue() {
            ReviewLike like = TestDataFactory.defaultReviewLike(testUser, testReview).build();
            entityManager.persist(like);
            entityManager.flush();

            boolean exists = reviewLikeRepository.existsByUserIdAndReviewId(
                    testUser.getId(), testReview.getId());
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("returns false when like does not exist")
        void existsByUserIdAndReviewId_NoLike_ReturnsFalse() {
            boolean exists = reviewLikeRepository.existsByUserIdAndReviewId(
                    testUser.getId(), testReview.getId());
            assertThat(exists).isFalse();
        }
    }


    @Nested
    @DisplayName("countByReviewId Tests")
    class CountByReviewIdTests {

        @Test
        @DisplayName("returns correct count")
        void countByReviewId_MultipleLikes_ReturnsCorrectCount() {
            ReviewLike like1 = TestDataFactory.defaultReviewLike(testUser, testReview).build();
            entityManager.persist(like1);

            User user3 = TestDataFactory.createUser("3");
            entityManager.persist(user3);

            ReviewLike like2 = TestDataFactory.defaultReviewLike(user3, testReview).build();
            entityManager.persist(like2);

            entityManager.flush();

            long count = reviewLikeRepository.countByReviewId(testReview.getId());
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("returns zero when no likes")
        void countByReviewId_NoLikes_ReturnsZero() {
            long count = reviewLikeRepository.countByReviewId(testReview.getId());
            assertThat(count).isZero();
        }
    }


    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("persists new like")
        void save_NewLike_PersistsLike() {
            ReviewLike like = TestDataFactory.defaultReviewLike(testUser, testReview).build();
            ReviewLike saved = reviewLikeRepository.save(like);
            entityManager.flush();

            assertThat(saved.getId()).isNotNull();
            ReviewLike found = entityManager.find(ReviewLike.class, saved.getId());
            assertThat(found).isNotNull();
        }
    }


    @Nested
    @DisplayName("delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("removes like")
        void delete_ExistingLike_RemovesLike() {
            ReviewLike like = TestDataFactory.defaultReviewLike(testUser, testReview).build();
            entityManager.persist(like);
            entityManager.flush();

            reviewLikeRepository.delete(like);
            entityManager.flush();

            ReviewLike found = entityManager.find(ReviewLike.class, like.getId());
            assertThat(found).isNull();
        }
    }
}

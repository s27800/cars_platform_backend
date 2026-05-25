package com.carsplatform.backend.api.fuelReportLikes;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("FuelReportLikeRepository Integration Tests")
class FuelReportLikeRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FuelReportLikeRepository fuelReportLikeRepository;

    private User testUser;
    private User anotherUser;
    private FuelReport testFuelReport;

    @BeforeEach
    void setUp() {

        // Create users
        testUser = TestDataFactory.defaultUser()
                .username("liker")
                .email("liker@example.com")
                .build();
        entityManager.persist(testUser);

        anotherUser = TestDataFactory.defaultUser()
                .username("reporter")
                .email("reporter@example.com")
                .build();
        entityManager.persist(anotherUser);

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .name("BMW")
                .build();

        entityManager.persist(brand);

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .build();

        entityManager.persist(model);

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .build();

        entityManager.persist(generation);

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .build();

        entityManager.persist(bodyType);

        // Create test car
        Car car = TestDataFactory.defaultCar(generation, bodyType)
                .build();

        entityManager.persist(car.getEngine());
        entityManager.persist(car.getTransmission());
        entityManager.persist(car.getChassis());
        entityManager.persist(car.getPerformance());
        entityManager.persist(car.getInsideDimensions());
        entityManager.persist(car.getOutsideDimensions());
        entityManager.persist(car);

        // Create fuel report
        testFuelReport = TestDataFactory.defaultFuelReport(anotherUser, car)
                .isApproved(true)
                .build();

        entityManager.persist(testFuelReport);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("findByUserIdAndFuelReportId Tests")
    class FindByUserIdAndFuelReportIdTests {

        @Test
        @DisplayName("returns like when exists")
        void findByUserIdAndFuelReportId_LikeExists_ReturnsLike() {

            // Create like
            FuelReportLike like = TestDataFactory.defaultFuelReportLike(testUser, testFuelReport).build();

            entityManager.persist(like);
            entityManager.flush();

            // Find like
            Optional<FuelReportLike> result = fuelReportLikeRepository.findByUserIdAndFuelReportId(
                    testUser.getId(), testFuelReport.getId());

            // Verify result -> like exists
            assertThat(result).isPresent();
            assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
            assertThat(result.get().getFuelReport().getId()).isEqualTo(testFuelReport.getId());
        }

        @Test
        @DisplayName("returns empty when like does not exist")
        void findByUserIdAndFuelReportId_NoLike_ReturnsEmpty() {

            // Find like that does not exist
            Optional<FuelReportLike> result = fuelReportLikeRepository.findByUserIdAndFuelReportId(
                    testUser.getId(), testFuelReport.getId());

            // Verify result -> like does not exist
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty for different user")
        void findByUserIdAndFuelReportId_DifferentUser_ReturnsEmpty() {

            // Create like for testUser
            FuelReportLike like = TestDataFactory.defaultFuelReportLike(testUser, testFuelReport).build();

            entityManager.persist(like);
            entityManager.flush();

            // Find like for different user
            Optional<FuelReportLike> result = fuelReportLikeRepository.findByUserIdAndFuelReportId(
                    anotherUser.getId(), testFuelReport.getId());

            // Verify result -> no like for other user
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("existsByUserIdAndFuelReportId Tests")
    class ExistsByUserIdAndFuelReportIdTests {

        @Test
        @DisplayName("returns true when like exists")
        void existsByUserIdAndFuelReportId_LikeExists_ReturnsTrue() {

            // Create like
            FuelReportLike like = TestDataFactory.defaultFuelReportLike(testUser, testFuelReport).build();

            entityManager.persist(like);
            entityManager.flush();

            // Check if like exists
            boolean result = fuelReportLikeRepository.existsByUserIdAndFuelReportId(
                    testUser.getId(), testFuelReport.getId());

            // Verify result -> like exists
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("returns false when like does not exist")
        void existsByUserIdAndFuelReportId_NoLike_ReturnsFalse() {

            // Check if like exists
            boolean result = fuelReportLikeRepository.existsByUserIdAndFuelReportId(
                    testUser.getId(), testFuelReport.getId());

            // Verify result -> like does not exist
            assertThat(result).isFalse();
        }
    }


    @Nested
    @DisplayName("countByFuelReportId Tests")
    class CountByFuelReportIdTests {

        @Test
        @DisplayName("returns correct count")
        void countByFuelReportId_MultipleLikes_ReturnsCount() {

            // Create likes from both users
            FuelReportLike like1 = TestDataFactory.defaultFuelReportLike(testUser, testFuelReport).build();
            FuelReportLike like2 = TestDataFactory.defaultFuelReportLike(anotherUser, testFuelReport).build();

            entityManager.persist(like1);
            entityManager.persist(like2);

            entityManager.flush();

            // Count likes
            long count = fuelReportLikeRepository.countByFuelReportId(testFuelReport.getId());

            // Verify result -> correct count
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("returns zero when no likes")
        void countByFuelReportId_NoLikes_ReturnsZero() {

            // Count likes
            long count = fuelReportLikeRepository.countByFuelReportId(testFuelReport.getId());

            // Verify result -> no likes
            assertThat(count).isZero();
        }
    }
}

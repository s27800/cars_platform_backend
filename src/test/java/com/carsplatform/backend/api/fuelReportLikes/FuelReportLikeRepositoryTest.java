package com.carsplatform.backend.api.fuelReportLikes;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
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
        Brand brand = TestDataFactory.defaultBrand()
                .name("BMW")
                .build();

        entityManager.persist(brand);
        Model model = TestDataFactory.defaultModel(brand)
                .build();

        entityManager.persist(model);
        Generation generation = TestDataFactory.defaultGeneration(model)
                .build();

        entityManager.persist(generation);
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .build();

        entityManager.persist(bodyType);
        Car car = TestDataFactory.defaultCar(generation, bodyType)
                .build();

        entityManager.persist(car.getEngine());
        entityManager.persist(car.getTransmission());
        entityManager.persist(car.getChassis());
        entityManager.persist(car.getPerformance());
        entityManager.persist(car.getInsideDimensions());
        entityManager.persist(car.getOutsideDimensions());
        entityManager.persist(car);

        testFuelReport = TestDataFactory.defaultFuelReport(anotherUser, car)
                .status(ModerationStatus.APPROVED)
                .build();

        entityManager.persist(testFuelReport);

        entityManager.flush();
    }


    @Nested
    @DisplayName("findByUserIdAndFuelReportId Tests")
    class FindByUserIdAndFuelReportIdTests {

        @Test
        @DisplayName("returns like when exists")
        void findByUserIdAndFuelReportId_LikeExists_ReturnsLike() {
            FuelReportLike like = TestDataFactory.defaultFuelReportLike(testUser, testFuelReport).build();

            entityManager.persist(like);
            entityManager.flush();

            Optional<FuelReportLike> result = fuelReportLikeRepository.findByUserIdAndFuelReportId(
                    testUser.getId(), testFuelReport.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getUser().getId()).isEqualTo(testUser.getId());
            assertThat(result.get().getFuelReport().getId()).isEqualTo(testFuelReport.getId());
        }

        @Test
        @DisplayName("returns empty when like does not exist")
        void findByUserIdAndFuelReportId_NoLike_ReturnsEmpty() {
            Optional<FuelReportLike> result = fuelReportLikeRepository.findByUserIdAndFuelReportId(
                    testUser.getId(), testFuelReport.getId());
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty for different user")
        void findByUserIdAndFuelReportId_DifferentUser_ReturnsEmpty() {
            FuelReportLike like = TestDataFactory.defaultFuelReportLike(testUser, testFuelReport).build();

            entityManager.persist(like);
            entityManager.flush();

            Optional<FuelReportLike> result = fuelReportLikeRepository.findByUserIdAndFuelReportId(
                    anotherUser.getId(), testFuelReport.getId());
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("existsByUserIdAndFuelReportId Tests")
    class ExistsByUserIdAndFuelReportIdTests {

        @Test
        @DisplayName("returns true when like exists")
        void existsByUserIdAndFuelReportId_LikeExists_ReturnsTrue() {
            FuelReportLike like = TestDataFactory.defaultFuelReportLike(testUser, testFuelReport).build();

            entityManager.persist(like);
            entityManager.flush();

            boolean result = fuelReportLikeRepository.existsByUserIdAndFuelReportId(
                    testUser.getId(), testFuelReport.getId());
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("returns false when like does not exist")
        void existsByUserIdAndFuelReportId_NoLike_ReturnsFalse() {
            boolean result = fuelReportLikeRepository.existsByUserIdAndFuelReportId(
                    testUser.getId(), testFuelReport.getId());
            assertThat(result).isFalse();
        }
    }


    @Nested
    @DisplayName("countByFuelReportId Tests")
    class CountByFuelReportIdTests {

        @Test
        @DisplayName("returns correct count")
        void countByFuelReportId_MultipleLikes_ReturnsCount() {
            FuelReportLike like1 = TestDataFactory.defaultFuelReportLike(testUser, testFuelReport).build();
            FuelReportLike like2 = TestDataFactory.defaultFuelReportLike(anotherUser, testFuelReport).build();

            entityManager.persist(like1);
            entityManager.persist(like2);

            entityManager.flush();

            long count = fuelReportLikeRepository.countByFuelReportId(testFuelReport.getId());
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("returns zero when no likes")
        void countByFuelReportId_NoLikes_ReturnsZero() {
            long count = fuelReportLikeRepository.countByFuelReportId(testFuelReport.getId());
            assertThat(count).isZero();
        }
    }
}

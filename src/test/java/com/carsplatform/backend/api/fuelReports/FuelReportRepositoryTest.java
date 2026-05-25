package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("FuelReportRepository Integration Tests")
class FuelReportRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private FuelReportRepository fuelReportRepository;

    private User testUser;
    private Car testCar;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .username("reporter")
                .email("reporter@example.com")
                .build();

        entityManager.persist(testUser);

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
        testCar = TestDataFactory.defaultCar(generation, bodyType)
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
    @DisplayName("findByCarIdAndIsApprovedTrue")
    class FindByCarIdAndIsApprovedTrueTests {

        @Test
        @DisplayName("returns only approved reports")
        void findByCarIdAndIsApprovedTrue_ApprovedExists_ReturnsOnlyApproved() {

            // Set up approved and pending fuel reports
            FuelReport approved = createFuelReport(new BigDecimal("7.5"), true);
            FuelReport pending = createFuelReport(new BigDecimal("8.0"), false);

            entityManager.persist(approved);
            entityManager.persist(pending);

            entityManager.flush();

            // Find approved reports for car
            Page<FuelReport> result = fuelReportRepository.findByCarIdAndIsApprovedTrue(
                    testCar.getId(), PageRequest.of(0, 10));

            // Verify result -> only approved reports are returned
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getIsApproved()).isTrue();
            assertThat(result.getContent().get(0).getFuelConsumption())
                    .isEqualByComparingTo(new BigDecimal("7.5"));
        }

        @Test
        @DisplayName("returns empty when no approved reports")
        void findByCarIdAndIsApprovedTrue_NoApproved_ReturnsEmpty() {

            // Set up pending fuel report
            FuelReport pending = createFuelReport(new BigDecimal("8.0"), false);

            entityManager.persist(pending);
            entityManager.flush();

            // Find approved reports for car
            Page<FuelReport> result = fuelReportRepository.findByCarIdAndIsApprovedTrue(
                    testCar.getId(), PageRequest.of(0, 10));

            // Verify result -> no approved reports are returned
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("returns paginated results")
        void findByCarIdAndIsApprovedTrue_MultipleReports_ReturnsPaginated() {

            // Set up multiple approved fuel reports
            for (int i = 0; i < 5; i++) {
                User user = TestDataFactory.createUser(String.valueOf(i));

                entityManager.persist(user);

                FuelReport report = FuelReport.builder()
                        .user(user)
                        .car(testCar)
                        .fuelConsumption(new BigDecimal("7." + i))
                        .isApproved(true)
                        .reportDate(LocalDateTime.now())
                        .build();

                entityManager.persist(report);
            }

            entityManager.flush();

            // Find approved reports for car
            Page<FuelReport> result = fuelReportRepository.findByCarIdAndIsApprovedTrue(
                    testCar.getId(), PageRequest.of(0, 3));

            // Verify result -> paginated results are returned
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(5);
        }

        @Test
        @DisplayName("fetches user eagerly")
        void findByCarIdAndIsApprovedTrue_WithUser_FetchesUserEagerly() {

            // Set up approved fuel report with user
            FuelReport report = createFuelReport(new BigDecimal("7.5"), true);

            entityManager.persist(report);
            entityManager.flush();
            entityManager.clear();

            // Find approved reports for car
            Page<FuelReport> result = fuelReportRepository.findByCarIdAndIsApprovedTrue(
                    testCar.getId(), PageRequest.of(0, 10));

            // Verify result -> user is fetched eagerly
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUser()).isNotNull();
            assertThat(result.getContent().get(0).getUser().getUsername()).isEqualTo("reporter");
        }
    }


    @Nested
    @DisplayName("findAverageFuelConsumptionForCarId")
    class FindAverageFuelConsumptionForCarIdTests {

        @Test
        @DisplayName("calculates average correctly")
        void findAverageFuelConsumptionForCarId_MultipleReports_CalculatesAverage() {

            // Set up multiple approved fuel reports
            FuelReport report1 = createFuelReport(new BigDecimal("7.0"), true);

            User user2 = TestDataFactory.createUser("2");
            entityManager.persist(user2);

            FuelReport report2 = FuelReport.builder()
                    .user(user2)
                    .car(testCar)
                    .fuelConsumption(new BigDecimal("9.0"))
                    .isApproved(true)
                    .reportDate(LocalDateTime.now())
                    .build();

            entityManager.persist(report1);
            entityManager.persist(report2);
            entityManager.flush();

            // Find average fuel consumption for car
            Optional<BigDecimal> result = fuelReportRepository.findAverageFuelConsumptionForCarId(testCar.getId());

            // Verify result -> average consumption is calculated correctly
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualByComparingTo(new BigDecimal("8.0"));
        }

        @Test
        @DisplayName("ignores non-approved reports")
        void findAverageFuelConsumptionForCarId_WithPending_IgnoresPending() {

            // Set up approved fuel report
            FuelReport approved = createFuelReport(new BigDecimal("7.0"), true);

            User user2 = TestDataFactory.createUser("2");
            entityManager.persist(user2);

            FuelReport pending = FuelReport.builder()
                    .user(user2)
                    .car(testCar)
                    .fuelConsumption(new BigDecimal("15.0"))
                    .isApproved(false)
                    .reportDate(LocalDateTime.now())
                    .build();

            entityManager.persist(approved);
            entityManager.persist(pending);
            entityManager.flush();

            // Find average fuel consumption for car
            Optional<BigDecimal> result = fuelReportRepository.findAverageFuelConsumptionForCarId(testCar.getId());

            // Verify result -> average consumption is calculated correctly
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualByComparingTo(new BigDecimal("7.0"));
        }

        @Test
        @DisplayName("returns empty when no approved reports")
        void findAverageFuelConsumptionForCarId_NoApproved_ReturnsEmpty() {

            // Set up pending fuel report
            FuelReport pending = createFuelReport(new BigDecimal("7.0"), false);

            entityManager.persist(pending);
            entityManager.flush();

            // Find average fuel consumption for car
            Optional<BigDecimal> result = fuelReportRepository.findAverageFuelConsumptionForCarId(testCar.getId());

            // Verify result -> result is empty
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty for car with no reports")
        void findAverageFuelConsumptionForCarId_NoReports_ReturnsEmpty() {

            // Find average fuel consumption for car
            Optional<BigDecimal> result = fuelReportRepository.findAverageFuelConsumptionForCarId(testCar.getId());

            // Verify result -> result is empty
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("CRUD operations")
    class CrudTests {

        @Test
        @DisplayName("save persists new fuel report")
        void save_NewReport_PersistsReport() {

            // Create new fuel report
            FuelReport report = createFuelReport(new BigDecimal("8.5"), false);

            // Save report
            FuelReport saved = fuelReportRepository.save(report);

            entityManager.flush();

            // Verify result -> report is saved
            assertThat(saved.getId()).isNotNull();

            FuelReport found = entityManager.find(FuelReport.class, saved.getId());

            assertThat(found.getFuelConsumption()).isEqualByComparingTo(new BigDecimal("8.5"));
        }

        @Test
        @DisplayName("delete removes fuel report")
        void delete_ExistingReport_RemovesReport() {

            // Set up existing fuel report
            FuelReport report = createFuelReport(new BigDecimal("7.0"), true);

            entityManager.persist(report);
            entityManager.flush();

            // Delete fuel report
            fuelReportRepository.delete(report);
            entityManager.flush();

            // Verify result -> report is removed
            FuelReport found = entityManager.find(FuelReport.class, report.getId());

            assertThat(found).isNull();
        }
    }


    // helper method

    private FuelReport createFuelReport(BigDecimal consumption, boolean isApproved) {
        return FuelReport.builder()
                .user(testUser)
                .car(testCar)
                .fuelConsumption(consumption)
                .comment("Test fuel report")
                .isApproved(isApproved)
                .reportDate(LocalDateTime.now())
                .build();
    }
}

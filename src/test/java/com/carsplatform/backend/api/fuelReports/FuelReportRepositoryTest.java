package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
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
        testUser = TestDataFactory.defaultUser()
                .username("reporter")
                .email("reporter@example.com")
                .build();

        entityManager.persist(testUser);
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
        testCar = TestDataFactory.defaultCar(generation, bodyType)
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
        @DisplayName("returns only approved reports")
        void findAllApprovedByCarId_ApprovedExists_ReturnsOnlyApproved() {
            FuelReport approved = createFuelReport(new BigDecimal("7.5"), ModerationStatus.APPROVED);
            FuelReport pending = createFuelReport(new BigDecimal("8.0"), ModerationStatus.PENDING);

            entityManager.persist(approved);
            entityManager.persist(pending);

            entityManager.flush();

            Page<FuelReport> result = fuelReportRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getStatus()).isEqualTo(ModerationStatus.APPROVED);
            assertThat(result.getContent().get(0).getFuelConsumption())
                    .isEqualByComparingTo(new BigDecimal("7.5"));
        }

        @Test
        @DisplayName("returns empty when no approved reports")
        void findAllApprovedByCarId_NoApproved_ReturnsEmpty() {
            FuelReport pending = createFuelReport(new BigDecimal("8.0"), ModerationStatus.PENDING);

            entityManager.persist(pending);
            entityManager.flush();

            Page<FuelReport> result = fuelReportRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 10));
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("returns paginated results")
        void findAllApprovedByCarId_MultipleReports_ReturnsPaginated() {
            for (int i = 0; i < 5; i++) {
                User user = TestDataFactory.createUser(String.valueOf(i));

                entityManager.persist(user);

                FuelReport report = FuelReport.builder()
                        .user(user)
                        .car(testCar)
                        .fuelConsumption(new BigDecimal("7." + i))
                        .status(ModerationStatus.APPROVED)
                        .reportDate(LocalDateTime.now())
                        .build();

                entityManager.persist(report);
            }

            entityManager.flush();

            Page<FuelReport> result = fuelReportRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 3));
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(5);
        }

        @Test
        @DisplayName("fetches user eagerly")
        void findAllApprovedByCarId_WithUser_FetchesUserEagerly() {
            FuelReport report = createFuelReport(new BigDecimal("7.5"), ModerationStatus.APPROVED);

            entityManager.persist(report);
            entityManager.flush();
            entityManager.clear();

            Page<FuelReport> result = fuelReportRepository.findAllApprovedByCarId(
                    testCar.getId(), PageRequest.of(0, 10));
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
            FuelReport report1 = createFuelReport(new BigDecimal("7.0"), ModerationStatus.APPROVED);

            User user2 = TestDataFactory.createUser("2");
            entityManager.persist(user2);

            FuelReport report2 = FuelReport.builder()
                    .user(user2)
                    .car(testCar)
                    .fuelConsumption(new BigDecimal("9.0"))
                    .status(ModerationStatus.APPROVED)
                    .reportDate(LocalDateTime.now())
                    .build();

            entityManager.persist(report1);
            entityManager.persist(report2);
            entityManager.flush();

            Optional<BigDecimal> result = fuelReportRepository.findAverageFuelConsumptionForCarId(testCar.getId());
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualByComparingTo(new BigDecimal("8.0"));
        }

        @Test
        @DisplayName("ignores non-approved reports")
        void findAverageFuelConsumptionForCarId_WithPending_IgnoresPending() {
            FuelReport approved = createFuelReport(new BigDecimal("7.0"), ModerationStatus.APPROVED);

            User user2 = TestDataFactory.createUser("2");
            entityManager.persist(user2);

            FuelReport pending = FuelReport.builder()
                    .user(user2)
                    .car(testCar)
                    .fuelConsumption(new BigDecimal("15.0"))
                    .status(ModerationStatus.PENDING)
                    .reportDate(LocalDateTime.now())
                    .build();

            entityManager.persist(approved);
            entityManager.persist(pending);
            entityManager.flush();

            Optional<BigDecimal> result = fuelReportRepository.findAverageFuelConsumptionForCarId(testCar.getId());
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualByComparingTo(new BigDecimal("7.0"));
        }

        @Test
        @DisplayName("returns empty when no approved reports")
        void findAverageFuelConsumptionForCarId_NoApproved_ReturnsEmpty() {
            FuelReport pending = createFuelReport(new BigDecimal("7.0"), ModerationStatus.PENDING);

            entityManager.persist(pending);
            entityManager.flush();

            Optional<BigDecimal> result = fuelReportRepository.findAverageFuelConsumptionForCarId(testCar.getId());
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty for car with no reports")
        void findAverageFuelConsumptionForCarId_NoReports_ReturnsEmpty() {
            Optional<BigDecimal> result = fuelReportRepository.findAverageFuelConsumptionForCarId(testCar.getId());
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("CRUD operations")
    class CrudTests {

        @Test
        @DisplayName("save persists new fuel report")
        void save_NewReport_PersistsReport() {
            FuelReport report = createFuelReport(new BigDecimal("8.5"), ModerationStatus.PENDING);

            FuelReport saved = fuelReportRepository.save(report);

            entityManager.flush();
            assertThat(saved.getId()).isNotNull();

            FuelReport found = entityManager.find(FuelReport.class, saved.getId());

            assertThat(found.getFuelConsumption()).isEqualByComparingTo(new BigDecimal("8.5"));
        }

        @Test
        @DisplayName("delete removes fuel report")
        void delete_ExistingReport_RemovesReport() {
            FuelReport report = createFuelReport(new BigDecimal("7.0"), ModerationStatus.APPROVED);

            entityManager.persist(report);
            entityManager.flush();

            fuelReportRepository.delete(report);
            entityManager.flush();
            FuelReport found = entityManager.find(FuelReport.class, report.getId());

            assertThat(found).isNull();
        }
    }


    private FuelReport createFuelReport(BigDecimal consumption, ModerationStatus status) {
        return FuelReport.builder()
                .user(testUser)
                .car(testCar)
                .fuelConsumption(consumption)
                .comment("Test fuel report")
                .status(status)
                .reportDate(LocalDateTime.now())
                .build();
    }
}

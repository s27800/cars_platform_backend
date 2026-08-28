package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.admin.dtos.AdminCarInfoResponse;
import com.carsplatform.backend.api.admin.dtos.AdminFuelReportResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AdminFuelReportMapper Tests")
class AdminFuelReportMapperTest {

    @Autowired
    private AdminFuelReportMapper adminFuelReportMapper;

    private User testUser;
    private Car testCar;
    private FuelReport testFuelReport;
    private Brand brand;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();

        // Create test brand
        brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("Toyota")
                .build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .name("Camry")
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .name("XV70")
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .name("Camry 2.5")
                .build();

        // Create test fuel report
        testFuelReport = TestDataFactory.defaultFuelReport(testUser, testCar)
                .id(UUID.randomUUID())
                .isApproved(false)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should map fuel report to AdminFuelReportResponse")
        void toDto_ValidFuelReport_ReturnsAdminFuelReportResponse() {

            // Map fuel report to DTO
            AdminFuelReportResponse result = adminFuelReportMapper.toDto(testFuelReport);

            // Verify results -> all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testFuelReport.getId());
            assertThat(result.getFuelConsumption()).isEqualTo(testFuelReport.getFuelConsumption());
            assertThat(result.getComment()).isEqualTo(testFuelReport.getComment());
            assertThat(result.getIsApproved()).isEqualTo(testFuelReport.getIsApproved());
            assertThat(result.getUsernameResponse()).isNotNull();
            assertThat(result.getUsernameResponse().getUsername()).isEqualTo(testUser.getUsername());
        }

        @Test
        @DisplayName("should map car info correctly")
        void toDto_ValidFuelReport_MapsCarInfoCorrectly() {

            // Map fuel report to DTO
            AdminFuelReportResponse result = adminFuelReportMapper.toDto(testFuelReport);

            // Verify car info -> all fields are mapped correctly
            assertThat(result.getCarInfo()).isNotNull();
            assertThat(result.getCarInfo().getCarId()).isEqualTo(testCar.getId());
            assertThat(result.getCarInfo().getCarName()).isEqualTo(testCar.getName());
            assertThat(result.getCarInfo().getBrandName()).isEqualTo("Toyota");
            assertThat(result.getCarInfo().getModelName()).isEqualTo("Camry");
            assertThat(result.getCarInfo().getGenerationName()).isEqualTo("XV70");
        }

        @Test
        @DisplayName("should return null when fuel report is null")
        void toDto_NullFuelReport_ReturnsNull() {

            // Map null fuel report to DTO
            AdminFuelReportResponse result = adminFuelReportMapper.toDto(null);

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
            AdminCarInfoResponse result = adminFuelReportMapper.toCarInfo(testCar);

            // Verify results -> all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getCarId()).isEqualTo(testCar.getId());
            assertThat(result.getCarName()).isEqualTo(testCar.getName());
            assertThat(result.getBrandName()).isEqualTo("Toyota");
            assertThat(result.getModelName()).isEqualTo("Camry");
            assertThat(result.getGenerationName()).isEqualTo("XV70");
        }

        @Test
        @DisplayName("should return null when car is null")
        void toCarInfo_NullCar_ReturnsNull() {

            // Map null car to DTO
            AdminCarInfoResponse result = adminFuelReportMapper.toCarInfo(null);

            // Verify result is null
            assertThat(result).isNull();
        }
    }


    @Nested
    @DisplayName("toDtoList")
    class ToDtoListTests {

        @Test
        @DisplayName("should map page of fuel reports to page of AdminFuelReportResponse")
        void toDtoList_ValidPage_ReturnsMappedPage() {

            // Create page of fuel reports
            Page<FuelReport> fuelReportPage = new PageImpl<>(List.of(testFuelReport));

            // Map page to DTOs
            Page<AdminFuelReportResponse> result = adminFuelReportMapper.toDtoList(fuelReportPage);

            // Verify results -> all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(testFuelReport.getId());
        }

        @Test
        @DisplayName("should return null when page is null")
        void toDtoList_NullPage_ReturnsNull() {

            // Map null page to DTOs
            Page<AdminFuelReportResponse> result = adminFuelReportMapper.toDtoList(null);

            // Verify result is null
            assertThat(result).isNull();
        }
    }
}

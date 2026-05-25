package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("FuelReportMapper Tests")
class FuelReportMapperTest {

    @Autowired
    private FuelReportMapper mapper;

    private User testUser;
    private Car testCar;
    private FuelReport testFuelReport;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(1L)
                .username("reporter")
                .build();

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .id(1)
                .build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .id(1)
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(1)
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(1)
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(1)
                .build();

        // Create test fuel report
        testFuelReport = TestDataFactory.defaultFuelReport(testUser, testCar)
                .id(1L)
                .fuelConsumption(new BigDecimal("7.8"))
                .comment("Good fuel economy")
                .reportDate(LocalDateTime.of(2024, 1, 15, 10, 30))
                .isApproved(true)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            FuelReportResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidFuelReport_MapsAllFields() {

            // Map valid fuel report
            FuelReportResponse result = mapper.toDto(testFuelReport);

            // Verify result -> all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFuelConsumption()).isEqualByComparingTo(new BigDecimal("7.8"));
            assertThat(result.getComment()).isEqualTo("Good fuel economy");
            assertThat(result.getReportDate()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
            assertThat(result.getIsApproved()).isTrue();
        }

        @Test
        @DisplayName("should map user to usernameResponse")
        void toDto_FuelReportWithUser_MapsUsernameResponse() {

            // Map fuel report with user
            FuelReportResponse result = mapper.toDto(testFuelReport);

            // Verify result -> usernameResponse is mapped correctly
            assertThat(result.getUsernameResponse()).isNotNull();
            assertThat(result.getUsernameResponse().getUsername()).isEqualTo("reporter");
        }

        @Test
        @DisplayName("should handle null user")
        void toDto_FuelReportWithNullUser_HandlesGracefully() {

            // Set user to null
            testFuelReport.setUser(null);

            // Map fuel report with null user
            FuelReportResponse result = mapper.toDto(testFuelReport);

            // Verify result -> usernameResponse is null
            assertThat(result).isNotNull();
            assertThat(result.getUsernameResponse()).isNull();
        }
    }


    @Nested
    @DisplayName("toDtoList")
    class ToDtoListTests {

        @Test
        @DisplayName("should return null when page is null")
        void toDtoList_NullPage_ReturnsNull() {

            // Map null page
            Page<FuelReportResponse> result = mapper.toDtoList(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map empty page")
        void toDtoList_EmptyPage_ReturnsEmptyPage() {

            // Set up empty page
            Page<FuelReport> emptyPage = Page.empty();

            // Map empty page
            Page<FuelReportResponse> result = mapper.toDtoList(emptyPage);

            // Verify result -> page is empty
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map page with fuel reports")
        void toDtoList_PageWithReports_MapsAllReports() {

            // Set up fuel reports
            FuelReport report1 = TestDataFactory.defaultFuelReport(testUser, testCar)
                    .id(1L)
                    .fuelConsumption(new BigDecimal("7.5"))
                    .build();

            FuelReport report2 = TestDataFactory.defaultFuelReport(testUser, testCar)
                    .id(2L)
                    .fuelConsumption(new BigDecimal("8.2"))
                    .build();

            Page<FuelReport> reportPage = new PageImpl<>(
                    List.of(report1, report2),
                    PageRequest.of(0, 10),
                    2
            );

            // Map page with fuel reports
            Page<FuelReportResponse> result = mapper.toDtoList(reportPage);

            // Verify result -> all reports are mapped correctly
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getFuelConsumption()).isEqualByComparingTo(new BigDecimal("7.5"));
            assertThat(result.getContent().get(1).getFuelConsumption()).isEqualByComparingTo(new BigDecimal("8.2"));
        }
    }
}

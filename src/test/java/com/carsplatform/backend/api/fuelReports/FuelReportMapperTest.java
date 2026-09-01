package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportResponse;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .username("reporter")
                .build();
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .build();
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .build();
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .build();
        testFuelReport = TestDataFactory.defaultFuelReport(testUser, testCar)
                .id(UUID.randomUUID())
                .fuelConsumption(new BigDecimal("7.8"))
                .comment("Good fuel economy")
                .reportDate(LocalDateTime.of(2024, 1, 15, 10, 30))
                .status(ModerationStatus.APPROVED)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            FuelReportResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidFuelReport_MapsAllFields() {
            FuelReportResponse result = mapper.toDto(testFuelReport);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testFuelReport.getId());
            assertThat(result.getFuelConsumption()).isEqualByComparingTo(new BigDecimal("7.8"));
            assertThat(result.getComment()).isEqualTo("Good fuel economy");
            assertThat(result.getReportDate()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
            assertThat(result.getStatus()).isEqualTo(ModerationStatus.APPROVED);
        }

        @Test
        @DisplayName("should map user to usernameResponse")
        void toDto_FuelReportWithUser_MapsUsernameResponse() {
            FuelReportResponse result = mapper.toDto(testFuelReport);
            assertThat(result.getUsernameResponse()).isNotNull();
            assertThat(result.getUsernameResponse().getUsername()).isEqualTo("reporter");
        }

        @Test
        @DisplayName("should handle null user")
        void toDto_FuelReportWithNullUser_HandlesGracefully() {
            testFuelReport.setUser(null);

            FuelReportResponse result = mapper.toDto(testFuelReport);
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
            Page<FuelReportResponse> result = mapper.toDtoList(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map empty page")
        void toDtoList_EmptyPage_ReturnsEmptyPage() {
            Page<FuelReport> emptyPage = Page.empty();

            Page<FuelReportResponse> result = mapper.toDtoList(emptyPage);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should map page with fuel reports")
        void toDtoList_PageWithReports_MapsAllReports() {
            FuelReport report1 = TestDataFactory.defaultFuelReport(testUser, testCar)
                    .id(UUID.randomUUID())
                    .fuelConsumption(new BigDecimal("7.5"))
                    .build();

            FuelReport report2 = TestDataFactory.defaultFuelReport(testUser, testCar)
                    .id(UUID.randomUUID())
                    .fuelConsumption(new BigDecimal("8.2"))
                    .build();

            Page<FuelReport> reportPage = new PageImpl<>(
                    List.of(report1, report2),
                    PageRequest.of(0, 10),
                    2
            );

            Page<FuelReportResponse> result = mapper.toDtoList(reportPage);
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getFuelConsumption()).isEqualByComparingTo(new BigDecimal("7.5"));
            assertThat(result.getContent().get(1).getFuelConsumption()).isEqualByComparingTo(new BigDecimal("8.2"));
        }
    }
}

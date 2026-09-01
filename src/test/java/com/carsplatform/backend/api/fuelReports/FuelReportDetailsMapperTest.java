package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportDetailsResponse;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("FuelReportDetailsMapper Tests")
class FuelReportDetailsMapperTest {

    @Autowired
    private FuelReportDetailsMapper fuelReportDetailsMapper;

    private User testUser;
    private Car testCar;
    private FuelReport testFuelReport;
    private Brand brand;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();
        brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("Toyota")
                .build();
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .name("Camry")
                .build();
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .name("XV70")
                .build();
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .name("Camry 2.5")
                .build();
        testFuelReport = TestDataFactory.defaultFuelReport(testUser, testCar)
                .id(UUID.randomUUID())
                .status(ModerationStatus.PENDING)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should map fuel report to FuelReportDetailsResponse")
        void toDto_ValidFuelReport_ReturnsFuelReportDetailsResponse() {
            FuelReportDetailsResponse result = fuelReportDetailsMapper.toDto(testFuelReport);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testFuelReport.getId());
            assertThat(result.getFuelConsumption()).isEqualTo(testFuelReport.getFuelConsumption());
            assertThat(result.getComment()).isEqualTo(testFuelReport.getComment());
            assertThat(result.getStatus()).isEqualTo(testFuelReport.getStatus());
            assertThat(result.getUsernameResponse()).isNotNull();
            assertThat(result.getUsernameResponse().getUsername()).isEqualTo(testUser.getUsername());
        }

        @Test
        @DisplayName("should map car info correctly")
        void toDto_ValidFuelReport_MapsCarInfoCorrectly() {
            FuelReportDetailsResponse result = fuelReportDetailsMapper.toDto(testFuelReport);

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
            FuelReportDetailsResponse result = fuelReportDetailsMapper.toDto(null);
            assertThat(result).isNull();
        }
    }


    @Nested
    @DisplayName("toDtoList")
    class ToDtoListTests {

        @Test
        @DisplayName("should map page of fuel reports to page of FuelReportDetailsResponse")
        void toDtoList_ValidPage_ReturnsMappedPage() {
            Page<FuelReport> fuelReportPage = new PageImpl<>(List.of(testFuelReport));

            Page<FuelReportDetailsResponse> result = fuelReportDetailsMapper.toDtoList(fuelReportPage);
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(testFuelReport.getId());
        }

        @Test
        @DisplayName("should return null when page is null")
        void toDtoList_NullPage_ReturnsNull() {
            Page<FuelReportDetailsResponse> result = fuelReportDetailsMapper.toDtoList(null);
            assertThat(result).isNull();
        }
    }
}

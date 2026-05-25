package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;


@DisplayName("CreateFuelReportMapper Tests")
class CreateFuelReportMapperTest {

    private final CreateFuelReportMapper mapper = Mappers.getMapper(CreateFuelReportMapper.class);

    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            FuelReport result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map request fields to entity")
        void toDto_ValidRequest_MapsFields() {

            // Create valid request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("7.5"))
                    .comment("City driving")
                    .build();

            // Map valid input
            FuelReport result = mapper.toDto(request);

            // Verify result -> fuelConsumption and comment are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getFuelConsumption()).isEqualByComparingTo(new BigDecimal("7.5"));
            assertThat(result.getComment()).isEqualTo("City driving");
        }

        @Test
        @DisplayName("should set isApproved to false")
        void toDto_ValidRequest_SetsIsApprovedFalse() {

            // Create valid request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("8.0"))
                    .build();

            // Map valid input
            FuelReport result = mapper.toDto(request);

            // Verify result -> isApproved is mapped correctly
            assertThat(result.getIsApproved()).isFalse();
        }

        @Test
        @DisplayName("should set reportDate to current time")
        void toDto_ValidRequest_SetsReportDate() {

            // Create valid request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("6.5"))
                    .build();

            // Map valid input
            FuelReport result = mapper.toDto(request);

            // Verify result -> reportDate is mapped correctly
            assertThat(result.getReportDate()).isNotNull();
        }

        @Test
        @DisplayName("should ignore id, user and car fields")
        void toDto_ValidRequest_IgnoresIdUserCar() {

            // Create valid request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("9.0"))
                    .build();

            // Map valid input
            FuelReport result = mapper.toDto(request);

            // Verify result -> id, user and car fields are ignored
            assertThat(result.getId()).isNull();
            assertThat(result.getUser()).isNull();
            assertThat(result.getCar()).isNull();
        }
    }
}

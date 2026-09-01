package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;
import com.carsplatform.backend.common.ModerationStatus;

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
            FuelReport result = mapper.toEntity(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map request fields to entity")
        void toDto_ValidRequest_MapsFields() {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("7.5"))
                    .comment("City driving")
                    .build();

            FuelReport result = mapper.toEntity(request);
            assertThat(result).isNotNull();
            assertThat(result.getFuelConsumption()).isEqualByComparingTo(new BigDecimal("7.5"));
            assertThat(result.getComment()).isEqualTo("City driving");
        }

        @Test
        @DisplayName("should set status to PENDING")
        void toDto_ValidRequest_SetsStatusPending() {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("8.0"))
                    .build();

            FuelReport result = mapper.toEntity(request);
            assertThat(result.getStatus()).isEqualTo(ModerationStatus.PENDING);
        }

        @Test
        @DisplayName("should set reportDate to current time")
        void toDto_ValidRequest_SetsReportDate() {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("6.5"))
                    .build();

            FuelReport result = mapper.toEntity(request);
            assertThat(result.getReportDate()).isNotNull();
        }

        @Test
        @DisplayName("should ignore id, user and car fields")
        void toDto_ValidRequest_IgnoresIdUserCar() {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("9.0"))
                    .build();

            FuelReport result = mapper.toEntity(request);
            assertThat(result.getId()).isNull();
            assertThat(result.getUser()).isNull();
            assertThat(result.getCar()).isNull();
        }
    }
}

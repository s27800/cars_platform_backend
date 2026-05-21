package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;


@DisplayName("AverageFuelConsumptionMapper Tests")
class AverageFuelConsumptionMapperTest {

    private final AverageFuelConsumptionMapper mapper = Mappers.getMapper(AverageFuelConsumptionMapper.class);

    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            AverageFuelConsumptionResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map BigDecimal to averageFuelConsumption")
        void toDto_ValidBigDecimal_MapsToAverageFuelConsumption() {

            // Create test BigDecimal
            BigDecimal consumption = new BigDecimal("7.5");

            // Map BigDecimal
            AverageFuelConsumptionResponse result = mapper.toDto(consumption);

            // Verify result is mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getAverageFuelConsumption()).isEqualByComparingTo(new BigDecimal("7.5"));
        }

        @Test
        @DisplayName("should handle zero consumption")
        void toDto_ZeroConsumption_MapsCorrectly() {

            // Create test BigDecimal
            BigDecimal zeroConsumption = BigDecimal.ZERO;

            // Map BigDecimal
            AverageFuelConsumptionResponse result = mapper.toDto(zeroConsumption);

            // Verify result is mapped correctly
            assertThat(result.getAverageFuelConsumption()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should handle high precision consumption")
        void toDto_HighPrecisionConsumption_MapsCorrectly() {

            // Create test BigDecimal
            BigDecimal preciseConsumption = new BigDecimal("8.123456");

            // Map BigDecimal
            AverageFuelConsumptionResponse result = mapper.toDto(preciseConsumption);

            // Verify result is mapped correctly
            assertThat(result.getAverageFuelConsumption()).isEqualByComparingTo(new BigDecimal("8.123456"));
        }
    }
}

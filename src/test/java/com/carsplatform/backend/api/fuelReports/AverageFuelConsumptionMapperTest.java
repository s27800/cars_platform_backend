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
            AverageFuelConsumptionResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map BigDecimal to averageFuelConsumption")
        void toDto_ValidBigDecimal_MapsToAverageFuelConsumption() {
            BigDecimal consumption = new BigDecimal("7.5");

            AverageFuelConsumptionResponse result = mapper.toDto(consumption);
            assertThat(result).isNotNull();
            assertThat(result.getAverageFuelConsumption()).isEqualByComparingTo(new BigDecimal("7.5"));
        }

        @Test
        @DisplayName("should handle zero consumption")
        void toDto_ZeroConsumption_MapsCorrectly() {
            BigDecimal zeroConsumption = BigDecimal.ZERO;

            AverageFuelConsumptionResponse result = mapper.toDto(zeroConsumption);
            assertThat(result.getAverageFuelConsumption()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should handle high precision consumption")
        void toDto_HighPrecisionConsumption_MapsCorrectly() {
            BigDecimal preciseConsumption = new BigDecimal("8.123456");

            AverageFuelConsumptionResponse result = mapper.toDto(preciseConsumption);
            assertThat(result.getAverageFuelConsumption()).isEqualByComparingTo(new BigDecimal("8.123456"));
        }
    }
}

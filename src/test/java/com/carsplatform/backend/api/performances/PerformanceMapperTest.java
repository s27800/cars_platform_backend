package com.carsplatform.backend.api.performances;

import com.carsplatform.backend.api.performances.dtos.CarPerformanceResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;


@DisplayName("PerformanceMapper Tests")
class PerformanceMapperTest {

    private final PerformanceMapper mapper = Mappers.getMapper(PerformanceMapper.class);

    private Performance testPerformance;

    @BeforeEach
    void setUp() {

        // Create test performance
        testPerformance = TestDataFactory.defaultPerformance()
                .id(1)
                .maxSpeed(250)
                .acceleration0100(new BigDecimal("7.2"))
                .acceleration100200(new BigDecimal("22.5"))
                .fuelTankCapacity(60)
                .fuelConsumptionCity(new BigDecimal("9.5"))
                .fuelConsumptionRoute(new BigDecimal("6.2"))
                .fuelConsumptionMixed(new BigDecimal("7.4"))
                .rangeCity(630)
                .rangeRoute(968)
                .rangeMixed(810)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null performance
            CarPerformanceResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidPerformance_MapsAllFields() {

            // Map valid performance
            CarPerformanceResponse result = mapper.toDto(testPerformance);

            // Verify all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getMaxSpeed()).isEqualTo(250);
            assertThat(result.getAcceleration0100()).isEqualByComparingTo(new BigDecimal("7.2"));
            assertThat(result.getAcceleration100200()).isEqualByComparingTo(new BigDecimal("22.5"));
            assertThat(result.getFuelTankCapacity()).isEqualTo(60);
            assertThat(result.getFuelConsumptionCity()).isEqualByComparingTo(new BigDecimal("9.5"));
            assertThat(result.getFuelConsumptionRoute()).isEqualByComparingTo(new BigDecimal("6.2"));
            assertThat(result.getFuelConsumptionMixed()).isEqualByComparingTo(new BigDecimal("7.4"));
            assertThat(result.getRangeCity()).isEqualTo(630);
            assertThat(result.getRangeRoute()).isEqualTo(968);
            assertThat(result.getRangeMixed()).isEqualTo(810);
        }

        @Test
        @DisplayName("should handle null optional fields")
        void toDto_PerformanceWithNulls_MapsNullFields() {

            // Create performance without optional fields
            Performance minimalPerformance = Performance.builder()
                    .id(1)
                    .maxSpeed(200)
                    .acceleration0100(null)
                    .build();

            // Map minimal performance
            CarPerformanceResponse result = mapper.toDto(minimalPerformance);

            // Verify null optional fields are handled correctly
            assertThat(result.getMaxSpeed()).isEqualTo(200);
            assertThat(result.getAcceleration0100()).isNull();
        }
    }
}

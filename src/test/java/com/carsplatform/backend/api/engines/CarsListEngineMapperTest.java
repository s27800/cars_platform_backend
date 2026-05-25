package com.carsplatform.backend.api.engines;

import com.carsplatform.backend.api.engines.dtos.CarsListEngineResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;


@DisplayName("CarsListEngineMapper Tests")
class CarsListEngineMapperTest {

    private final CarsListEngineMapper mapper = Mappers.getMapper(CarsListEngineMapper.class);

    private Engine testEngine;

    @BeforeEach
    void setUp() {

        // Create test engine
        testEngine = TestDataFactory.defaultEngine()
                .id(1)
                .engineType("Petrol")
                .displacement(1984)
                .maxPower(190)
                .build();
    }

    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            CarsListEngineResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map only engineType, displacement and maxPower")
        void toDto_ValidEngine_MapsSelectedFields() {

            // Map valid engine
            CarsListEngineResponse result = mapper.toDto(testEngine);

            // Verify all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getEngineType()).isEqualTo("Petrol");
            assertThat(result.getDisplacement()).isEqualTo(1984);
            assertThat(result.getMaxPower()).isEqualTo(190);
        }

        @Test
        @DisplayName("should handle diesel engine")
        void toDto_DieselEngine_MapsCorrectly() {

            // Create test diesel engine
            Engine diesel = TestDataFactory.defaultEngine()
                    .engineType("Diesel")
                    .displacement(2993)
                    .maxPower(265)
                    .build();

            // Map diesel engine
            CarsListEngineResponse result = mapper.toDto(diesel);

            // Verify diesel engine is mapped correctly
            assertThat(result.getEngineType()).isEqualTo("Diesel");
            assertThat(result.getDisplacement()).isEqualTo(2993);
            assertThat(result.getMaxPower()).isEqualTo(265);
        }
    }
}

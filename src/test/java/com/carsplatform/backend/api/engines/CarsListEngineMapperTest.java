package com.carsplatform.backend.api.engines;

import com.carsplatform.backend.api.engines.dtos.CarsListEngineResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("CarsListEngineMapper Tests")
class CarsListEngineMapperTest {

    private final CarsListEngineMapper mapper = Mappers.getMapper(CarsListEngineMapper.class);

    private Engine testEngine;

    @BeforeEach
    void setUp() {
        testEngine = TestDataFactory.defaultEngine()
                .id(UUID.randomUUID())
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
            CarsListEngineResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map only engineType, displacement and maxPower")
        void toDto_ValidEngine_MapsSelectedFields() {
            CarsListEngineResponse result = mapper.toDto(testEngine);

            assertThat(result).isNotNull();
            assertThat(result.getEngineType()).isEqualTo("Petrol");
            assertThat(result.getDisplacement()).isEqualTo(1984);
            assertThat(result.getMaxPower()).isEqualTo(190);
        }

        @Test
        @DisplayName("should handle diesel engine")
        void toDto_DieselEngine_MapsCorrectly() {
            Engine diesel = TestDataFactory.defaultEngine()
                    .engineType("Diesel")
                    .displacement(2993)
                    .maxPower(265)
                    .build();

            CarsListEngineResponse result = mapper.toDto(diesel);

            assertThat(result.getEngineType()).isEqualTo("Diesel");
            assertThat(result.getDisplacement()).isEqualTo(2993);
            assertThat(result.getMaxPower()).isEqualTo(265);
        }
    }
}

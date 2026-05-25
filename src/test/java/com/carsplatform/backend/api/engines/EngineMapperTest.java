package com.carsplatform.backend.api.engines;

import com.carsplatform.backend.api.engines.dtos.CarEngineResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;


@DisplayName("EngineMapper Tests")
class EngineMapperTest {

    private final EngineMapper mapper = Mappers.getMapper(EngineMapper.class);

    private Engine testEngine;

    @BeforeEach
    void setUp() {

        // Create test engine
        testEngine = TestDataFactory.defaultEngine()
                .id(1)
                .engineCode("2.0 TSI")
                .productionYears("2015-2020")
                .displacement(1984)
                .engineType("Petrol")
                .maxPower(190)
                .maxPowerRotationSpeed(4200)
                .turbo("Turbocharger")
                .cylindersNumber(4)
                .cylindersLayout("Inline")
                .valvesNumber(16)
                .ignition("Direct Injection")
                .injectionType("Direct")
                .maxTorque(320)
                .maxTorqueRotationSpeed(1500)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            CarEngineResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidEngine_MapsAllFields() {

            // Map valid engine
            CarEngineResponse result = mapper.toDto(testEngine);

            // Verify all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getEngineCode()).isEqualTo("2.0 TSI");
            assertThat(result.getProductionYears()).isEqualTo("2015-2020");
            assertThat(result.getDisplacement()).isEqualTo(1984);
            assertThat(result.getEngineType()).isEqualTo("Petrol");
            assertThat(result.getMaxPower()).isEqualTo(190);
            assertThat(result.getMaxPowerRotationSpeed()).isEqualTo(4200);
            assertThat(result.getTurbo()).isEqualTo("Turbocharger");
            assertThat(result.getCylindersNumber()).isEqualTo(4);
            assertThat(result.getCylindersLayout()).isEqualTo("Inline");
            assertThat(result.getValvesNumber()).isEqualTo(16);
            assertThat(result.getIgnition()).isEqualTo("Direct Injection");
            assertThat(result.getInjectionType()).isEqualTo("Direct");
            assertThat(result.getMaxTorque()).isEqualTo(320);
            assertThat(result.getMaxTorqueRotationSpeed()).isEqualTo(1500);
        }

        @Test
        @DisplayName("should handle null optional fields")
        void toDto_EngineWithNullFields_MapsNullFields() {

            // Create minimal engine with null optional fields
            Engine minimalEngine = Engine.builder()
                    .id(1)
                    .engineCode(null)
                    .turbo(null)
                    .build();

            // Map minimal engine
            CarEngineResponse result = mapper.toDto(minimalEngine);

            // Verify null fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getEngineCode()).isNull();
            assertThat(result.getTurbo()).isNull();
        }
    }
}

package com.carsplatform.backend.api.transmissions;

import com.carsplatform.backend.api.transmissions.dtos.CarsListTransmissionResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;


@DisplayName("CarsListTransmissionMapper Tests")
class CarsListTransmissionMapperTest {

    private final CarsListTransmissionMapper mapper = Mappers.getMapper(CarsListTransmissionMapper.class);

    private Transmission testTransmission;

    @BeforeEach
    void setUp() {

        // Create test transmission
        testTransmission = TestDataFactory.defaultTransmission()
                .id(1)
                .transmissionType("Automatic")
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            CarsListTransmissionResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map only transmissionType")
        void toDto_ValidTransmission_MapsTransmissionType() {

            // Map valid transmission
            CarsListTransmissionResponse result = mapper.toDto(testTransmission);

            // Verify all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getTransmissionType()).isEqualTo("Automatic");
        }

        @Test
        @DisplayName("should handle manual transmission")
        void toDto_ManualTransmission_MapsCorrectly() {

            // Create manual transmission
            Transmission manual = Transmission.builder()
                    .transmissionType("Manual")
                    .build();

            // Map manual transmission
            CarsListTransmissionResponse result = mapper.toDto(manual);

            // Verify result is mapped correctly
            assertThat(result.getTransmissionType()).isEqualTo("Manual");
        }
    }
}

package com.carsplatform.backend.api.transmissions;

import com.carsplatform.backend.api.transmissions.dtos.CarsListTransmissionResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("CarsListTransmissionMapper Tests")
class CarsListTransmissionMapperTest {

    private final CarsListTransmissionMapper mapper = Mappers.getMapper(CarsListTransmissionMapper.class);

    private Transmission testTransmission;

    @BeforeEach
    void setUp() {
        testTransmission = TestDataFactory.defaultTransmission()
                .id(UUID.randomUUID())
                .transmissionType("Automatic")
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            CarsListTransmissionResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map only transmissionType")
        void toDto_ValidTransmission_MapsTransmissionType() {
            CarsListTransmissionResponse result = mapper.toDto(testTransmission);

            assertThat(result).isNotNull();
            assertThat(result.getTransmissionType()).isEqualTo("Automatic");
        }

        @Test
        @DisplayName("should handle manual transmission")
        void toDto_ManualTransmission_MapsCorrectly() {
            Transmission manual = Transmission.builder()
                    .transmissionType("Manual")
                    .build();

            CarsListTransmissionResponse result = mapper.toDto(manual);
            assertThat(result.getTransmissionType()).isEqualTo("Manual");
        }
    }
}

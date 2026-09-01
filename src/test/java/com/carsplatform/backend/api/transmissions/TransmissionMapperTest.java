package com.carsplatform.backend.api.transmissions;

import com.carsplatform.backend.api.transmissions.dtos.CarTransmissionResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("TransmissionMapper Tests")
class TransmissionMapperTest {

    private final TransmissionMapper mapper = Mappers.getMapper(TransmissionMapper.class);

    private Transmission testTransmission;

    @BeforeEach
    void setUp() {
        testTransmission = TestDataFactory.defaultTransmission()
                .id(UUID.randomUUID())
                .transmissionType("Automatic")
                .transmissionName("DSG 7")
                .gearsNumber(7)
                .clutchType("Dual-clutch")
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            CarTransmissionResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidTransmission_MapsAllFields() {
            CarTransmissionResponse result = mapper.toDto(testTransmission);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testTransmission.getId());
            assertThat(result.getTransmissionType()).isEqualTo("Automatic");
            assertThat(result.getTransmissionName()).isEqualTo("DSG 7");
            assertThat(result.getGearsNumber()).isEqualTo(7);
            assertThat(result.getClutchType()).isEqualTo("Dual-clutch");
        }

        @Test
        @DisplayName("should handle manual transmission")
        void toDto_ManualTransmission_MapsCorrectly() {
            Transmission manual = Transmission.builder()
                    .id(UUID.randomUUID())
                    .transmissionType("Manual")
                    .transmissionName("6-speed manual")
                    .gearsNumber(6)
                    .clutchType("Single-disc")
                    .build();

            CarTransmissionResponse result = mapper.toDto(manual);
            assertThat(result.getTransmissionType()).isEqualTo("Manual");
            assertThat(result.getGearsNumber()).isEqualTo(6);
        }
    }
}

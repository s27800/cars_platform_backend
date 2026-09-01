package com.carsplatform.backend.api.chassis;

import com.carsplatform.backend.api.chassis.dtos.CarChassisResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("ChassisMapper Tests")
class ChassisMapperTest {

    private final ChassisMapper mapper = Mappers.getMapper(ChassisMapper.class);

    private Chassis testChassis;

    @BeforeEach
    void setUp() {
        testChassis = TestDataFactory.defaultChassis()
                .id(UUID.randomUUID())
                .basicRims("17\"")
                .optionalRims("18\", 19\"")
                .basicTires("225/45 R17")
                .optionalTires("235/40 R18")
                .frontBrakes("Ventilated disc")
                .backBrakes("Disc")
                .frontBrakesRadius(312)
                .backBrakesRadius(286)
                .frontBrakesThickness(25)
                .backBrakesThickness(12)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {
        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            CarChassisResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidChassis_MapsAllFields() {
            CarChassisResponse result = mapper.toDto(testChassis);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testChassis.getId());
            assertThat(result.getBasicRims()).isEqualTo("17\"");
            assertThat(result.getOptionalRims()).isEqualTo("18\", 19\"");
            assertThat(result.getBasicTires()).isEqualTo("225/45 R17");
            assertThat(result.getOptionalTires()).isEqualTo("235/40 R18");
            assertThat(result.getFrontBrakes()).isEqualTo("Ventilated disc");
            assertThat(result.getBackBrakes()).isEqualTo("Disc");
            assertThat(result.getFrontBrakesRadius()).isEqualTo(312);
            assertThat(result.getBackBrakesRadius()).isEqualTo(286);
            assertThat(result.getFrontBrakesThickness()).isEqualTo(25);
            assertThat(result.getBackBrakesThickness()).isEqualTo(12);
        }

        @Test
        @DisplayName("should handle null optional fields")
        void toDto_ChassisWithNulls_MapsNullFields() {
            Chassis minimalChassis = Chassis.builder()
                    .id(UUID.randomUUID())
                    .basicRims("17\"")
                    .optionalRims(null)
                    .build();

            CarChassisResponse result = mapper.toDto(minimalChassis);

            assertThat(result.getBasicRims()).isEqualTo("17\"");
            assertThat(result.getOptionalRims()).isNull();
        }
    }
}

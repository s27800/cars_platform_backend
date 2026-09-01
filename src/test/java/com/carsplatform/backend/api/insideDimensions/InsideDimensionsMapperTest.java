package com.carsplatform.backend.api.insideDimensions;

import com.carsplatform.backend.api.insideDimensions.dtos.CarInsideDimensionsResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("InsideDimensionsMapper Tests")
class InsideDimensionsMapperTest {

    private final InsideDimensionsMapper mapper = Mappers.getMapper(InsideDimensionsMapper.class);

    private InsideDimensions testInsideDimensions;

    @BeforeEach
    void setUp() {
        testInsideDimensions = TestDataFactory.defaultInsideDimensions()
                .id(UUID.randomUUID())
                .heightFromSeatToRoofFront(1020)
                .heightFromSeatToRoofBack(960)
                .maxTrunkSpace(1680)
                .minTrunkSpace(590)
                .minTrunkLength(1020)
                .maxTrunkLength(1920)
                .trunkWidth(1050)
                .trunkHeight(500)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            CarInsideDimensionsResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidInsideDimensions_MapsAllFields() {
            CarInsideDimensionsResponse result = mapper.toDto(testInsideDimensions);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testInsideDimensions.getId());
            assertThat(result.getHeightFromSeatToRoofFront()).isEqualTo(1020);
            assertThat(result.getHeightFromSeatToRoofBack()).isEqualTo(960);
            assertThat(result.getMaxTrunkSpace()).isEqualTo(1680);
            assertThat(result.getMinTrunkSpace()).isEqualTo(590);
            assertThat(result.getMinTrunkLength()).isEqualTo(1020);
            assertThat(result.getMaxTrunkLength()).isEqualTo(1920);
            assertThat(result.getTrunkWidth()).isEqualTo(1050);
            assertThat(result.getTrunkHeight()).isEqualTo(500);
        }

        @Test
        @DisplayName("should handle null optional fields")
        void toDto_InsideDimensionsWithNulls_MapsNullFields() {
            InsideDimensions minimalDimensions = InsideDimensions.builder()
                    .id(UUID.randomUUID())
                    .heightFromSeatToRoofFront(1000)
                    .build();

            CarInsideDimensionsResponse result = mapper.toDto(minimalDimensions);

            assertThat(result.getId()).isEqualTo(minimalDimensions.getId());
            assertThat(result.getHeightFromSeatToRoofFront()).isEqualTo(1000);
            assertThat(result.getMaxTrunkSpace()).isNull();
        }
    }
}

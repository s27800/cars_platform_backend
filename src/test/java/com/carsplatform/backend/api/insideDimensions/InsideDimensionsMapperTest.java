package com.carsplatform.backend.api.insideDimensions;

import com.carsplatform.backend.api.insideDimensions.dtos.CarInsideDimensionsResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;


@DisplayName("InsideDimensionsMapper Tests")
class InsideDimensionsMapperTest {

    private final InsideDimensionsMapper mapper = Mappers.getMapper(InsideDimensionsMapper.class);

    private InsideDimensions testInsideDimensions;

    @BeforeEach
    void setUp() {

        // Create test inside dimensions
        testInsideDimensions = TestDataFactory.defaultInsideDimensions()
                .id(1)
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

            // Map null input
            CarInsideDimensionsResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidInsideDimensions_MapsAllFields() {

            // Map valid inside dimensions
            CarInsideDimensionsResponse result = mapper.toDto(testInsideDimensions);

            // Verify all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
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

            // Create inside dimensions with only required fields
            InsideDimensions minimalDimensions = InsideDimensions.builder()
                    .id(1)
                    .heightFromSeatToRoofFront(1000)
                    .build();

            // Map minimal inside dimensions
            CarInsideDimensionsResponse result = mapper.toDto(minimalDimensions);

            // Verify null optional fields are handled correctly
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getHeightFromSeatToRoofFront()).isEqualTo(1000);
            assertThat(result.getMaxTrunkSpace()).isNull();
        }
    }
}

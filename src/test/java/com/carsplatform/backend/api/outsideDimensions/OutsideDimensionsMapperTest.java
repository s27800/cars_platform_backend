package com.carsplatform.backend.api.outsideDimensions;

import com.carsplatform.backend.api.outsideDimensions.dtos.CarOutsideDimensionsResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("OutsideDimensionsMapper Tests")
class OutsideDimensionsMapperTest {

    private final OutsideDimensionsMapper mapper = Mappers.getMapper(OutsideDimensionsMapper.class);

    private OutsideDimensions testOutsideDimensions;

    @BeforeEach
    void setUp() {
        testOutsideDimensions = TestDataFactory.defaultOutsideDimensions()
                .id(UUID.randomUUID())
                .length(4584)
                .height(1456)
                .width(1832)
                .widthWithMirrors(2018)
                .heightWithOpenTrunk(1986)
                .wheelBase(2731)
                .wheelBaseFront(1563)
                .wheelBaseBack(1537)
                .overhangFront(889)
                .overhangBack(964)
                .clearance(115)
                .maxRoofLoad(75)
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {
        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            CarOutsideDimensionsResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void toDto_ValidOutsideDimensions_MapsAllFields() {
            CarOutsideDimensionsResponse result = mapper.toDto(testOutsideDimensions);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testOutsideDimensions.getId());
            assertThat(result.getLength()).isEqualTo(4584);
            assertThat(result.getHeight()).isEqualTo(1456);
            assertThat(result.getWidth()).isEqualTo(1832);
            assertThat(result.getWidthWithMirrors()).isEqualTo(2018);
            assertThat(result.getHeightWithOpenTrunk()).isEqualTo(1986);
            assertThat(result.getWheelBase()).isEqualTo(2731);
            assertThat(result.getWheelBaseFront()).isEqualTo(1563);
            assertThat(result.getWheelBaseBack()).isEqualTo(1537);
            assertThat(result.getOverhangFront()).isEqualTo(889);
            assertThat(result.getOverhangBack()).isEqualTo(964);
            assertThat(result.getClearance()).isEqualTo(115);
            assertThat(result.getMaxRoofLoad()).isEqualTo(75);
        }

        @Test
        @DisplayName("should handle null optional fields")
        void toDto_OutsideDimensionsWithNulls_MapsNullFields() {
            OutsideDimensions minimalDimensions = OutsideDimensions.builder()
                    .id(UUID.randomUUID())
                    .length(4500)
                    .height(1400)
                    .width(1800)
                    .build();

            CarOutsideDimensionsResponse result = mapper.toDto(minimalDimensions);

            assertThat(result.getLength()).isEqualTo(4500);
            assertThat(result.getWidthWithMirrors()).isNull();
        }
    }
}

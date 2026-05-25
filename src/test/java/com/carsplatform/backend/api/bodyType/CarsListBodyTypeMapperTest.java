package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.bodyType.dtos.CarsListBodyTypeResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;


@DisplayName("CarsListBodyTypeMapper Tests")
class CarsListBodyTypeMapperTest {

    private final CarsListBodyTypeMapper mapper = Mappers.getMapper(CarsListBodyTypeMapper.class);

    private BodyType testBodyType;

    @BeforeEach
    void setUp() {
        // Create test body type
        testBodyType = TestDataFactory.defaultBodyType()
                .id(1)
                .name("Sedan")
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            CarsListBodyTypeResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map only name")
        void toDto_ValidBodyType_MapsName() {

            // Map valid body type
            CarsListBodyTypeResponse result = mapper.toDto(testBodyType);

            // Verify result is valid
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Sedan");
        }

        @Test
        @DisplayName("should handle different body types")
        void toDto_DifferentBodyTypes_MapsCorrectly() {

            // Create different body types
            BodyType suv = TestDataFactory.defaultBodyType().name("SUV").build();
            BodyType hatchback = TestDataFactory.defaultBodyType().name("Hatchback").build();

            // Map different body types
            CarsListBodyTypeResponse suvResult = mapper.toDto(suv);
            CarsListBodyTypeResponse hatchbackResult = mapper.toDto(hatchback);

            // Verify results -> correct names mapped
            assertThat(suvResult.getName()).isEqualTo("SUV");
            assertThat(hatchbackResult.getName()).isEqualTo("Hatchback");
        }
    }
}

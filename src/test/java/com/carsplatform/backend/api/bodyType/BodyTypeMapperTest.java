package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.bodyType.dtos.CarBodyTypeResponse;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("BodyTypeMapper Tests")
class BodyTypeMapperTest {

    private final BodyTypeMapper mapper = Mappers.getMapper(BodyTypeMapper.class);

    private BodyType testBodyType;

    @BeforeEach
    void setUp() {
        testBodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .name("Sedan")
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class toDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {
            CarBodyTypeResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map id and name correctly")
        void toDto_ValidBodyType_MapsAllFields() {
            CarBodyTypeResponse result = mapper.toDto(testBodyType);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testBodyType.getId());
            assertThat(result.getName()).isEqualTo("Sedan");
        }

        @Test
        @DisplayName("should map different body types")
        void toDto_DifferentBodyTypes_MapsCorrectly() {
            BodyType suv = TestDataFactory.defaultBodyType()
                    .id(UUID.randomUUID())
                    .name("SUV")
                    .build();

            BodyType hatchback = TestDataFactory.defaultBodyType()
                    .id(UUID.randomUUID())
                    .name("Hatchback")
                    .build();

            CarBodyTypeResponse suvResult = mapper.toDto(suv);
            CarBodyTypeResponse hatchbackResult = mapper.toDto(hatchback);
            assertThat(suvResult.getName()).isEqualTo("SUV");
            assertThat(hatchbackResult.getName()).isEqualTo("Hatchback");
        }
    }
}

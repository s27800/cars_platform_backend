package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;


@DisplayName("BrandsListMapper Tests")
class BrandsListMapperTest {

    private final BrandsListMapper mapper = Mappers.getMapper(BrandsListMapper.class);

    private Brand testBrand;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = Brand.builder()
                .id(1)
                .name("Tesla")
                .country("USA")
                .foundedYear(2003)
                .description("Electric vehicle manufacturer")
                .build();
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void toDto_NullInput_ReturnsNull() {

            // Map null input
            BrandsListResponse result = mapper.toDto(null);

            // Verify result -> null is returned
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map only id and name")
        void toDto_ValidBrand_MapsIdAndName() {

            // Map valid brand
            BrandsListResponse result = mapper.toDto(testBrand);

            // Verify result -> brand is mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getName()).isEqualTo("Tesla");
        }
    }
}

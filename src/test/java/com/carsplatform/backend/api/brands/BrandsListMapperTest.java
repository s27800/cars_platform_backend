package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DisplayName("BrandsListMapper Tests")
class BrandsListMapperTest {

    private final BrandsListMapper mapper = Mappers.getMapper(BrandsListMapper.class);

    private Brand testBrand;

    @BeforeEach
    void setUp() {
        testBrand = Brand.builder()
                .id(UUID.randomUUID())
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
            BrandsListResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map only id and name")
        void toDto_ValidBrand_MapsIdAndName() {
            BrandsListResponse result = mapper.toDto(testBrand);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testBrand.getId());
            assertThat(result.getName()).isEqualTo("Tesla");
        }
    }
}

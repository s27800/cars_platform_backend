package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandDetailsResponse;
import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("BrandService Tests")
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private BrandsListMapper brandsListMapper;

    @Mock
    private BrandDetailsMapper brandDetailsMapper;

    @InjectMocks
    private BrandService brandService;

    private Brand testBrand;

    @BeforeEach
    void setUp() {
        testBrand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("BMW")
                .country("Germany")
                .foundedYear(1916)
                .build();
    }


    @Nested
    @DisplayName("getAllBrands")
    class GetAllBrandsTests {

        @Test
        @DisplayName("should return list of all brands")
        void getAllBrands_BrandsExist_ReturnsList() {
            Brand brand2 = TestDataFactory.defaultBrand()
                    .id(UUID.randomUUID())
                    .name("Mercedes")
                    .build();

            BrandsListResponse response1 = BrandsListResponse.builder()
                    .id(UUID.randomUUID())
                    .name("BMW")
                    .build();
            BrandsListResponse response2 = BrandsListResponse.builder()
                    .id(UUID.randomUUID())
                    .name("Mercedes")
                    .build();

            when(brandRepository.findAll()).thenReturn(List.of(testBrand, brand2));
            when(brandsListMapper.toDto(testBrand)).thenReturn(response1);
            when(brandsListMapper.toDto(brand2)).thenReturn(response2);

            List<BrandsListResponse> result = brandService.getAllBrands();
            assertThat(result).hasSize(2);
            assertThat(result).extracting(BrandsListResponse::getName)
                    .containsExactly("BMW", "Mercedes");

            verify(brandRepository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no brands exist")
        void getAllBrands_NoBrands_ReturnsEmptyList() {
            when(brandRepository.findAll()).thenReturn(Collections.emptyList());

            List<BrandsListResponse> result = brandService.getAllBrands();
            assertThat(result).isEmpty();
            verify(brandRepository).findAll();
        }
    }


    @Nested
    @DisplayName("getBrandDetailsById")
    class GetBrandDetailsByIdTests {

        @Test
        @DisplayName("should return brand details when brand exists")
        void getBrandDetailsById_BrandExists_ReturnsDetails() {
            UUID testUuid = UUID.randomUUID();

            BrandDetailsResponse expectedResponse = BrandDetailsResponse.builder()
                    .id(testUuid)
                    .name("BMW")
                    .country("Germany")
                    .foundedYear(1916)
                    .build();

            when(brandRepository.findById(testBrand.getId())).thenReturn(Optional.of(testBrand));
            when(brandDetailsMapper.toDto(testBrand)).thenReturn(expectedResponse);

            BrandDetailsResponse result = brandService.getBrandDetailsById(testBrand.getId());
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testUuid);
            assertThat(result.getName()).isEqualTo("BMW");
            assertThat(result.getCountry()).isEqualTo("Germany");
            assertThat(result.getFoundedYear()).isEqualTo(1916);

            verify(brandRepository).findById(testBrand.getId());
            verify(brandDetailsMapper).toDto(testBrand);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when brand does not exist")
        void getBrandDetailsById_BrandNotFound_ThrowsException() {
            UUID nonExistentId = UUID.randomUUID();

            when(brandRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> brandService.getBrandDetailsById(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(brandRepository).findById(nonExistentId);
            verify(brandDetailsMapper, never()).toDto(any());
        }
    }
}

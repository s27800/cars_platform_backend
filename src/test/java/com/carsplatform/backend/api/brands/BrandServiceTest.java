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

        // Create test brand
        testBrand = TestDataFactory.defaultBrand()
                .id(1)
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

            // Create additional brand and expected responses
            Brand brand2 = TestDataFactory.defaultBrand()
                    .id(2)
                    .name("Mercedes")
                    .build();

            BrandsListResponse response1 = BrandsListResponse.builder()
                    .id(1)
                    .name("BMW")
                    .build();
            BrandsListResponse response2 = BrandsListResponse.builder()
                    .id(2)
                    .name("Mercedes")
                    .build();

            // Mock repository and mapper behavior
            when(brandRepository.findAll()).thenReturn(List.of(testBrand, brand2));
            when(brandsListMapper.toDto(testBrand)).thenReturn(response1);
            when(brandsListMapper.toDto(brand2)).thenReturn(response2);

            // Get all brands
            List<BrandsListResponse> result = brandService.getAllBrands();

            // Verify results -> correct number of brands and fields are correct
            assertThat(result).hasSize(2);
            assertThat(result).extracting(BrandsListResponse::getName)
                    .containsExactly("BMW", "Mercedes");

            verify(brandRepository).findAll();
        }

        @Test
        @DisplayName("should return empty list when no brands exist")
        void getAllBrands_NoBrands_ReturnsEmptyList() {

            // Mock repository to return empty list
            when(brandRepository.findAll()).thenReturn(Collections.emptyList());

            // Get all brands
            List<BrandsListResponse> result = brandService.getAllBrands();

            // Verify results -> empty list is returned
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

            // Create expected response
            BrandDetailsResponse expectedResponse = BrandDetailsResponse.builder()
                    .id(1)
                    .name("BMW")
                    .country("Germany")
                    .foundedYear(1916)
                    .build();

            // Mock repository and mapper behavior
            when(brandRepository.findById(1)).thenReturn(Optional.of(testBrand));
            when(brandDetailsMapper.toDto(testBrand)).thenReturn(expectedResponse);

            // Get brand details by ID
            BrandDetailsResponse result = brandService.getBrandDetailsById(1);

            // Verify results -> correct brand details are returned
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getName()).isEqualTo("BMW");
            assertThat(result.getCountry()).isEqualTo("Germany");
            assertThat(result.getFoundedYear()).isEqualTo(1916);

            verify(brandRepository).findById(1);
            verify(brandDetailsMapper).toDto(testBrand);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when brand does not exist")
        void getBrandDetailsById_BrandNotFound_ThrowsException() {

            // Mock repository to return empty optional
            when(brandRepository.findById(999)).thenReturn(Optional.empty());

            // Get brand details by ID and verify -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> brandService.getBrandDetailsById(999))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(brandRepository).findById(999);
            verify(brandDetailsMapper, never()).toDto(any());
        }
    }
}

package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;
import com.carsplatform.backend.api.brands.dtos.BrandDetailsResponse;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandsListMapper brandsListMapper;
    private final BrandDetailsMapper brandDetailsMapper;


    @Transactional(readOnly = true)
    public List<BrandsListResponse> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(brandsListMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BrandDetailsResponse getBrandDetailsById(UUID brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", brandId));

        return brandDetailsMapper.toDto(brand);
    }
}

package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;

import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface BrandsListMapper {
    BrandsListResponse toDto(Brand brand);
}

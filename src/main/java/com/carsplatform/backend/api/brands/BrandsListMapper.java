package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BrandsListMapper {
    BrandsListMapper INSTANCE = Mappers.getMapper(BrandsListMapper.class);

    BrandsListResponse toDto(Brand brand);
}

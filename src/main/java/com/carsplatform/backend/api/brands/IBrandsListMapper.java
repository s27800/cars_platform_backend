package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IBrandsListMapper {
    IBrandsListMapper INSTANCE = Mappers.getMapper(IBrandsListMapper.class);

    BrandsListResponse toDto(Brand brand);
}

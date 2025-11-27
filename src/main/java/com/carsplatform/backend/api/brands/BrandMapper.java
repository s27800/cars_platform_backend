package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.CarBrandResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandMapper INSTANCE = Mappers.getMapper(BrandMapper.class);
    CarBrandResponse toBrandDto(Brand brand);
}

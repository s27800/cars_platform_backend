package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandDetailsResponse;
import com.carsplatform.backend.api.models.IModelsListMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {IModelsListMapper.class})
public interface IBrandDetailsMapper {
    IBrandDetailsMapper INSTANCE = Mappers.getMapper(IBrandDetailsMapper.class);

    @Mapping(target = "models", qualifiedByName = "map", source = "brand")
    BrandDetailsResponse toDto(Brand brand);
}

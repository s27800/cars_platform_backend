package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandDetailsResponse;
import com.carsplatform.backend.api.models.ModelsListMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ModelsListMapper.class})
public interface BrandDetailsMapper {
    BrandDetailsMapper INSTANCE = Mappers.getMapper(BrandDetailsMapper.class);

    @Mapping(target = "models", qualifiedByName = "map", source = "brand")
    BrandDetailsResponse toDto(Brand brand);
}

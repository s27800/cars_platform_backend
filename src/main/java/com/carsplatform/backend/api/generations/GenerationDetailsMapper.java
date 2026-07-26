package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.brands.BrandsListMapper;
import com.carsplatform.backend.api.cars.CarsListMapper;
import com.carsplatform.backend.api.generations.dtos.GenerationDetailsResponse;
import com.carsplatform.backend.api.models.ModelsListMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CarsListMapper.class, ModelsListMapper.class, BrandsListMapper.class})
public interface GenerationDetailsMapper {
    GenerationDetailsMapper INSTANCE = Mappers.getMapper(GenerationDetailsMapper.class);

    @Mapping(target = "cars", qualifiedByName = "map", source = "generation")
    @Mapping(target = "model", source = "generation.model")
    @Mapping(target = "brand", source = "generation.model.brand")
    GenerationDetailsResponse toDto(Generation generation);
}

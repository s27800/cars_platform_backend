package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.cars.CarsListMapper;
import com.carsplatform.backend.api.generations.dtos.GenerationDetailsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CarsListMapper.class})
public interface GenerationDetailsMapper {
    GenerationDetailsMapper INSTANCE = Mappers.getMapper(GenerationDetailsMapper.class);

    @Mapping(target = "cars", qualifiedByName = "map", source = "generation")
    GenerationDetailsResponse toDto(Generation generation);
}

package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.generations.dtos.GenerationDetailsResponse;
import com.carsplatform.backend.api.cars.ICarsListMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ICarsListMapper.class})
public interface IGenerationDetailsMapper {
    IGenerationDetailsMapper INSTANCE = Mappers.getMapper(IGenerationDetailsMapper.class);

    @Mapping(target = "cars", qualifiedByName = "map", source = "generation")
    GenerationDetailsResponse toDto(Generation generation);
}

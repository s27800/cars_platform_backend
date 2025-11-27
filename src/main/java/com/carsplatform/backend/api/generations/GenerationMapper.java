package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.generations.dtos.CarGenerationResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GenerationMapper {
    GenerationMapper INSTANCE = Mappers.getMapper(GenerationMapper.class);
    CarGenerationResponse toDto(Generation generation);
}

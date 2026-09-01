package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.generations.dtos.CarGenerationResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface GenerationMapper {

    @Named("toDto")
    CarGenerationResponse toDto(Generation generation);
}

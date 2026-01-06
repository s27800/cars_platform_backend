package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.models.dtos.ModelDetailsResponse;
import com.carsplatform.backend.api.generations.GenerationsListMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {GenerationsListMapper.class})
public interface ModelDetailsMapper {
    ModelDetailsMapper INSTANCE = Mappers.getMapper(ModelDetailsMapper.class);

    @Mapping(target = "generations", qualifiedByName = "map", source = "model")
    ModelDetailsResponse toDto(Model model);
}

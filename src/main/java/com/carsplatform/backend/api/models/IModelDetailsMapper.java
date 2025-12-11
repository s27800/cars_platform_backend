package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.models.dtos.ModelDetailsResponse;
import com.carsplatform.backend.api.generations.IGenerationsListMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {IGenerationsListMapper.class})
public interface IModelDetailsMapper {
    IModelDetailsMapper INSTANCE = Mappers.getMapper(IModelDetailsMapper.class);

    @Mapping(target = "generations", qualifiedByName = "map", source = "model")
    ModelDetailsResponse toDto(Model model);
}

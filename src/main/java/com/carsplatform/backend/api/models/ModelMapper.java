package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.models.dtos.CarModelResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ModelMapper {
    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);
    CarModelResponse toModelDto(Model model);
}

package com.carsplatform.backend.api.engines;

import com.carsplatform.backend.api.engines.dtos.CarEngineResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IEngineMapper {
    IEngineMapper INSTANCE = Mappers.getMapper(IEngineMapper.class);

    @Named("toDto")
    CarEngineResponse toDto(Engine engine);
}

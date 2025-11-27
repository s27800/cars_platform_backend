package com.carsplatform.backend.api.engines;

import com.carsplatform.backend.api.engines.dtos.CarEngineResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EngineMapper {
    EngineMapper INSTANCE = Mappers.getMapper(EngineMapper.class);

    @Named("toDto")
    CarEngineResponse toDto(Engine engine);
}

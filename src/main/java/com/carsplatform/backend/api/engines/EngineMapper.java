package com.carsplatform.backend.api.engines;

import com.carsplatform.backend.api.engines.dtos.CarEngineResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface EngineMapper {

    @Named("toDto")
    CarEngineResponse toDto(Engine engine);
}

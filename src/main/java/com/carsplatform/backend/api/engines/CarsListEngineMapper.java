package com.carsplatform.backend.api.engines;

import com.carsplatform.backend.api.engines.dtos.CarsListEngineResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface CarsListEngineMapper {

    @Named("toDto")
    CarsListEngineResponse toDto(Engine engine);
}

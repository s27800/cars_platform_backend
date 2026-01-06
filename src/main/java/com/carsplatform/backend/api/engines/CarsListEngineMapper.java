package com.carsplatform.backend.api.engines;

import com.carsplatform.backend.api.engines.dtos.CarsListEngineResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarsListEngineMapper {
    CarsListEngineMapper INSTANCE = Mappers.getMapper(CarsListEngineMapper.class);

    @Named("toDto")
    CarsListEngineResponse toDto(Engine engine);
}

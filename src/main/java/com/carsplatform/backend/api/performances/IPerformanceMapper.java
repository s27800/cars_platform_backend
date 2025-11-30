package com.carsplatform.backend.api.performances;

import com.carsplatform.backend.api.performances.dtos.CarPerformanceResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IPerformanceMapper {
    IPerformanceMapper INSTANCE = Mappers.getMapper(IPerformanceMapper.class);

    @Named("toDto")
    CarPerformanceResponse toDto(Performance performance);
}

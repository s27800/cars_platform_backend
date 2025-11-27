package com.carsplatform.backend.api.performances;

import com.carsplatform.backend.api.performances.dtos.CarPerformanceResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {
    PerformanceMapper INSTANCE = Mappers.getMapper(PerformanceMapper.class);
    CarPerformanceResponse toDto(Performance performance);
}

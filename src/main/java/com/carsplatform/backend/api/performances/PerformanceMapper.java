package com.carsplatform.backend.api.performances;

import com.carsplatform.backend.api.performances.dtos.CarPerformanceResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface PerformanceMapper {

    @Named("toDto")
    CarPerformanceResponse toDto(Performance performance);
}

package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface IAverageFuelConsumptionMapper {
    IAverageFuelConsumptionMapper INSTANCE = Mappers.getMapper(IAverageFuelConsumptionMapper.class);

    @Mapping(target = "averageFuelConsumption", source = "avgConsumption")
    AverageFuelConsumptionResponse toDto(BigDecimal avgConsumption);
}

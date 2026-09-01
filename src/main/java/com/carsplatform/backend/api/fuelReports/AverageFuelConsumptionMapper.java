package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;


@Mapper(componentModel = "spring")
public interface AverageFuelConsumptionMapper {

    @Mapping(target = "averageFuelConsumption", source = "avgConsumption")
    AverageFuelConsumptionResponse toDto(BigDecimal avgConsumption);
}

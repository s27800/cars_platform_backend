package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CreateFuelReportMapper {
    CreateFuelReportMapper INSTANCE = Mappers.getMapper(CreateFuelReportMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "isApproved", constant = "false")
    @Mapping(target = "reportDate", expression = "java(java.time.LocalDateTime.now())")
    FuelReport toDto(CreateFuelReportRequest fuelReportRequest);
}

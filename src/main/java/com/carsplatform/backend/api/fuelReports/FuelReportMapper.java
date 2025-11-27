package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.CarFuelReportResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FuelReportMapper {
    FuelReportMapper INSTANCE = Mappers.getMapper(FuelReportMapper.class);
    CarFuelReportResponse toFuelReportDto(FuelReport fuelReport);
}

package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.CarFuelReportResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FuelReportMapper {
    FuelReportMapper INSTANCE = Mappers.getMapper(FuelReportMapper.class);

    @Mapping(target = "usernameResponse", source = "user")
    CarFuelReportResponse toDto(FuelReport fuelReport);

    default List<CarFuelReportResponse> toDtoList(List<FuelReport> reports) {
        if (reports == null)
            return null;

        return reports.stream().map(this::toDto).collect(Collectors.toList());
    }
}

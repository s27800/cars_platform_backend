package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.FuelReportResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import org.springframework.data.domain.Page;


@Mapper(componentModel = "spring")
public interface FuelReportMapper {

    @Mapping(target = "usernameResponse", source = "user")
    FuelReportResponse toDto(FuelReport fuelReport);

    @Named("toDtoList")
    default Page<FuelReportResponse> toDtoList(Page<FuelReport> reports) {
        if (reports == null)
            return null;

        return reports.map(this::toDto);
    }
}

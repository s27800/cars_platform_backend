package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;
import com.carsplatform.backend.common.ModerationStatus;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", imports = {ModerationStatus.class})
public interface CreateFuelReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "status", expression = "java(ModerationStatus.PENDING)")
    @Mapping(target = "reportDate", expression = "java(java.time.LocalDateTime.now())")
    FuelReport toEntity(CreateFuelReportRequest fuelReportRequest);
}

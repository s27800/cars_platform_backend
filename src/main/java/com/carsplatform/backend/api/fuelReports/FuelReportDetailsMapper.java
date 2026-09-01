package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.cars.CarInfoMapper;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportDetailsResponse;
import com.carsplatform.backend.api.users.UsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import org.springframework.data.domain.Page;


@Mapper(componentModel = "spring", uses = {UsernameMapper.class, CarInfoMapper.class})
public interface FuelReportDetailsMapper {

    @Mapping(target = "usernameResponse", source = "user")
    @Mapping(target = "carInfo", source = "car", qualifiedByName = "toCarInfo")
    FuelReportDetailsResponse toDto(FuelReport fuelReport);

    @Named("toDtoList")
    default Page<FuelReportDetailsResponse> toDtoList(Page<FuelReport> fuelReports) {
        if (fuelReports == null)
            return null;

        return fuelReports.map(this::toDto);
    }
}

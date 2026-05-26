package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.admin.dtos.AdminCarInfoResponse;
import com.carsplatform.backend.api.admin.dtos.AdminFuelReportResponse;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.users.UsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {UsernameMapper.class})
public interface AdminFuelReportMapper {
    AdminFuelReportMapper INSTANCE = Mappers.getMapper(AdminFuelReportMapper.class);

    @Mapping(target = "usernameResponse", source = "user")
    @Mapping(target = "carInfo", source = "car", qualifiedByName = "toCarInfo")
    AdminFuelReportResponse toDto(FuelReport fuelReport);

    @Named("toCarInfo")
    default AdminCarInfoResponse toCarInfo(Car car) {
        if (car == null) {
            return null;
        }

        return AdminCarInfoResponse.builder()
                .carId(car.getId())
                .carName(car.getName())
                .brandName(car.getGeneration().getModel().getBrand().getName())
                .modelName(car.getGeneration().getModel().getName())
                .generationName(car.getGeneration().getName())
                .build();
    }

    @Named("toDtoList")
    default Page<AdminFuelReportResponse> toDtoList(Page<FuelReport> fuelReports) {
        if (fuelReports == null)
            return null;

        return fuelReports.map(this::toDto);
    }
}

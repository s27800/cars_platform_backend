package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.admin.dtos.AdminCarInfoResponse;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GetDataProposalsMapper {
    GetDataProposalsMapper INSTANCE = Mappers.getMapper(GetDataProposalsMapper.class);

    @Mapping(target = "carInfo", source = "car", qualifiedByName = "toCarInfo")
    GetDataProposalsResponse toDto(DataProposal proposal);

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
}

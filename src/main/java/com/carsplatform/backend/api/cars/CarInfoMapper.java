package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.cars.dtos.CarInfoResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface CarInfoMapper {

    @Named("toCarInfo")
    default CarInfoResponse toCarInfo(Car car) {
        if (car == null)
            return null;

        return CarInfoResponse.builder()
                .carId(car.getId())
                .carName(car.getName())
                .brandName(car.getGeneration().getModel().getBrand().getName())
                .modelName(car.getGeneration().getModel().getName())
                .generationName(car.getGeneration().getName())
                .build();
    }
}

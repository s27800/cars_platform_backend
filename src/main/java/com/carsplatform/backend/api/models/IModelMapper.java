package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.models.dtos.CarModelResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IModelMapper {
    IModelMapper INSTANCE = Mappers.getMapper(IModelMapper.class);

    CarModelResponse toDto(Model model);

    @Named("map")
    default CarModelResponse map(Car car) {
        if (car == null)
            return null;

        return toDto(car.getGeneration().getModel());
    }
}

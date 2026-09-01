package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.CarBrandResponse;
import com.carsplatform.backend.api.cars.Car;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface BrandMapper {

    CarBrandResponse toDto(Brand brand);


    @Named("map")
    default CarBrandResponse map(Car car) {
        if (car == null)
            return null;

        return toDto(car.getGeneration().getModel().getBrand());
    }
}

package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.cars.dtos.CarsListResponse;
import com.carsplatform.backend.api.generations.Generation;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ICarsListMapper {
    ICarsListMapper INSTANCE = Mappers.getMapper(ICarsListMapper.class);

    CarsListResponse toDto(Car car);

    @Named("map")
    default List<CarsListResponse> map(Generation generation) {
        if (generation == null)
            return null;

        return generation.getCars().stream().map(this::toDto).collect(Collectors.toList());
    }
}

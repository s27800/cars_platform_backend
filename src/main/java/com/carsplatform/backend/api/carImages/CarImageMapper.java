package com.carsplatform.backend.api.carImages;

import com.carsplatform.backend.api.carImages.dtos.CarImageResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarImageMapper {
    CarImageMapper INSTANCE = Mappers.getMapper(CarImageMapper.class);
    CarImageResponse toCarImageDto(CarImage carImage);
}

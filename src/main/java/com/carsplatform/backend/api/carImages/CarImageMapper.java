package com.carsplatform.backend.api.carImages;

import com.carsplatform.backend.api.carImages.dtos.CarImageResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CarImageMapper {
    CarImageMapper INSTANCE = Mappers.getMapper(CarImageMapper.class);

    CarImageResponse toDto(CarImage carImage);

    default List<CarImageResponse> toDtoList(List<CarImage> images) {
        if (images == null)
            return null;

        return images.stream().map(this::toDto).collect(Collectors.toList());
    }
}

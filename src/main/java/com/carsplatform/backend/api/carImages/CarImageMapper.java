package com.carsplatform.backend.api.carImages;

import com.carsplatform.backend.api.carImages.dtos.CarImageResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface CarImageMapper {

    CarImageResponse toDto(CarImage carImage);


    @Named("toDtoList")
    default List<CarImageResponse> toDtoList(Collection<CarImage> images) {
        if (images == null)
            return null;

        return images.stream().map(this::toDto).collect(Collectors.toList());
    }
}

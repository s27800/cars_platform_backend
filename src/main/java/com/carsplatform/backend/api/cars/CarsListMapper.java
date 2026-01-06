package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.CarsListBodyTypeMapper;
import com.carsplatform.backend.api.carImages.CarImage;
import com.carsplatform.backend.api.cars.dtos.CarsListResponse;
import com.carsplatform.backend.api.engines.CarsListEngineMapper;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.transmissions.CarsListTransmissionMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {
        CarsListEngineMapper.class,
        CarsListBodyTypeMapper.class,
        CarsListTransmissionMapper.class
})
public interface CarsListMapper {
    CarsListMapper INSTANCE = Mappers.getMapper(CarsListMapper.class);

    @Mapping(target = "engine", qualifiedByName = "toDto")
    @Mapping(target = "bodyType", qualifiedByName = "toDto")
    @Mapping(target = "transmission", qualifiedByName = "toDto")
    @Mapping(target = "imageUrl", source = "car", qualifiedByName = "extractMainImage")
    CarsListResponse toDto(Car car);

    @Named("map")
    default List<CarsListResponse> map(Generation generation) {
        if (generation == null)
            return null;

        return generation.getCars().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Named("map")
    default Page<CarsListResponse> map(Page<Car> cars) {
        return cars.map(this::toDto);
    }

    @Named("extractMainImage")
    default String extractMainImage(Car car) {
        if (car.getImages() == null || car.getImages().isEmpty()) return null;
        return car.getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsMain()))
                .findFirst()
                .map(CarImage::getImageUrl)
                .orElse(car.getImages().get(0).getImageUrl());
    }
}

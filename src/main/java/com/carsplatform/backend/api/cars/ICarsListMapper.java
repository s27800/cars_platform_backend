package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.ICarsListBodyTypeMapper;
import com.carsplatform.backend.api.cars.dtos.CarsListResponse;
import com.carsplatform.backend.api.engines.ICarsListEngineMapper;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.transmissions.ICarsListTransmissionMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {
        ICarsListEngineMapper.class,
        ICarsListBodyTypeMapper.class,
        ICarsListTransmissionMapper.class
})
public interface ICarsListMapper {
    ICarsListMapper INSTANCE = Mappers.getMapper(ICarsListMapper.class);

    @Mapping(target = "engine", qualifiedByName = "toDto")
    @Mapping(target = "bodyType", qualifiedByName = "toDto")
    @Mapping(target = "transmission", qualifiedByName = "toDto")
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
}

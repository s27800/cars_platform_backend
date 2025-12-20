package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.IBodyTypeMapper;
import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;

import com.carsplatform.backend.api.brands.IBrandMapper;
import com.carsplatform.backend.api.carImages.ICarImageMapper;
import com.carsplatform.backend.api.chassis.IChassisMapper;
import com.carsplatform.backend.api.engines.IEngineMapper;
import com.carsplatform.backend.api.generations.IGenerationMapper;
import com.carsplatform.backend.api.insideDimensions.IInsideDimensionsMapper;
import com.carsplatform.backend.api.models.IModelMapper;
import com.carsplatform.backend.api.outsideDimensions.IOutsideDimensionsMapper;
import com.carsplatform.backend.api.performances.IPerformanceMapper;
import com.carsplatform.backend.api.tags.ITagMapper;
import com.carsplatform.backend.api.transmissions.ITransmissionMapper;
import com.carsplatform.backend.api.users.IUsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        IUsernameMapper.class,
        IBrandMapper.class,
        IModelMapper.class,
        IGenerationMapper.class,
        IBodyTypeMapper.class,
        IEngineMapper.class,
        IChassisMapper.class,
        ITransmissionMapper.class,
        IPerformanceMapper.class,
        IInsideDimensionsMapper.class,
        IOutsideDimensionsMapper.class,
        ICarImageMapper.class,
        ITagMapper.class
})
public interface ICarDetailsMapper {
    @Mapping(target = "brand", qualifiedByName = "map", source = "car")
    @Mapping(target = "model", qualifiedByName = "map", source = "car")
    @Mapping(target = "generation", qualifiedByName = "toDto")
    @Mapping(target = "bodyType", qualifiedByName = "toDto")
    @Mapping(target = "engine", qualifiedByName = "toDto")
    @Mapping(target = "chassis", qualifiedByName = "toDto")
    @Mapping(target = "transmission", qualifiedByName = "toDto")
    @Mapping(target = "performance", qualifiedByName = "toDto")
    @Mapping(target = "insideDimensions", qualifiedByName = "toDto")
    @Mapping(target = "outsideDimensions", qualifiedByName = "toDto")
    @Mapping(target = "images", qualifiedByName = "toDtoList")
    @Mapping(target = "tags", qualifiedByName = "toDtoList")
    CarDetailsResponse toDto(Car car);
}

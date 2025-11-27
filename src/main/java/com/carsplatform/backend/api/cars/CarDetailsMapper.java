package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;

import com.carsplatform.backend.api.bodyType.BodyTypeMapper;
import com.carsplatform.backend.api.brands.BrandMapper;
import com.carsplatform.backend.api.carImages.CarImageMapper;
import com.carsplatform.backend.api.chassis.ChassisMapper;
import com.carsplatform.backend.api.engines.EngineMapper;
import com.carsplatform.backend.api.fuelReports.FuelReportMapper;
import com.carsplatform.backend.api.generations.GenerationMapper;
import com.carsplatform.backend.api.insideDimensions.InsideDimensionsMapper;
import com.carsplatform.backend.api.models.ModelMapper;
import com.carsplatform.backend.api.outsideDimensions.OutsideDimensionsMapper;
import com.carsplatform.backend.api.performances.PerformanceMapper;
import com.carsplatform.backend.api.reviews.ReviewMapper;
import com.carsplatform.backend.api.tags.TagMapper;
import com.carsplatform.backend.api.transmissions.TransmissionMapper;
import com.carsplatform.backend.api.users.UsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        UsernameMapper.class,
        BrandMapper.class,
        ModelMapper.class,
        GenerationMapper.class,
        BodyTypeMapper.class,
        EngineMapper.class,
        ChassisMapper.class,
        TransmissionMapper.class,
        PerformanceMapper.class,
        InsideDimensionsMapper.class,
        OutsideDimensionsMapper.class,
        CarImageMapper.class,
        TagMapper.class,
        ReviewMapper.class,
        FuelReportMapper.class
})
public interface CarDetailsMapper {
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
    @Mapping(target = "reviews", qualifiedByName = "toDtoList")
    @Mapping(target = "fuelReports", qualifiedByName = "toDtoList")
    CarDetailsResponse toDto(Car car);
}

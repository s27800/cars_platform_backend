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
    @Mapping(target = "brand", expression = "java(brandMapper.toDto(car.getGeneration().getModel().getBrand()))")
    @Mapping(target = "model", expression = "java(modelMapper.toDto(car.getGeneration().getModel()))")
    @Mapping(target = "generation", expression = "java(generationMapper.toDto(car.getGeneration()))")
    @Mapping(target = "bodyType", expression = "java(bodyTypeMapper.toDto(car.getBodyType()))")
    @Mapping(target = "engine", expression = "java(engineMapper.toDto(car.getEngine()))")
    @Mapping(target = "chassis", expression = "java(chassisMapper.toDto(car.getChassis()))")
    @Mapping(target = "transmission", expression = "java(transmissionMapper.toDto(car.getTransmission()))")
    @Mapping(target = "performance", expression = "java(performanceMapper.toDto(car.getPerformance()))")
    @Mapping(target = "insideDimensions", expression = "java(insideDimensionsMapper.toDto(car.getInsideDimensions()))")
    @Mapping(target = "outsideDimensions", expression = "java(outsideDimensionsMapper.toDto(car.getOutsideDimensions()))")
    @Mapping(target = "images", expression = "java(carImageMapper.toDtoList(car.getImages()))")
    @Mapping(target = "tags", expression = "java(tagMapper.toDtoList(car.getTags()))")
    @Mapping(target = "reviews", qualifiedByName = "java(reviewMapper.toDtoList(car.getReviews()))")
    @Mapping(target = "fuelReports", qualifiedByName = "java(fuelReportMapper.toDtoList(car.getFuelReports()))")
    CarDetailsResponse toDto(Car car);
}

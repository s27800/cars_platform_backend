package com.carsplatform.backend.api.chassis;

import com.carsplatform.backend.api.chassis.dtos.CarChassisResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ChassisMapper {
    ChassisMapper INSTANCE = Mappers.getMapper(ChassisMapper.class);
    CarChassisResponse toDto(Chassis chassis);
}

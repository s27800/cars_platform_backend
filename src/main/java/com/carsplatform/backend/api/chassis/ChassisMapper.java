package com.carsplatform.backend.api.chassis;

import com.carsplatform.backend.api.chassis.dtos.CarChassisResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface ChassisMapper {

    @Named("toDto")
    CarChassisResponse toDto(Chassis chassis);
}

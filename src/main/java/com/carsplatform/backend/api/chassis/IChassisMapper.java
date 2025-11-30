package com.carsplatform.backend.api.chassis;

import com.carsplatform.backend.api.chassis.dtos.CarChassisResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IChassisMapper {
    IChassisMapper INSTANCE = Mappers.getMapper(IChassisMapper.class);

    @Named("toDto")
    CarChassisResponse toDto(Chassis chassis);
}

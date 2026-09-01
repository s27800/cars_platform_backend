package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.bodyType.dtos.CarsListBodyTypeResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface CarsListBodyTypeMapper {

    @Named("toDto")
    CarsListBodyTypeResponse toDto(BodyType bodyType);
}

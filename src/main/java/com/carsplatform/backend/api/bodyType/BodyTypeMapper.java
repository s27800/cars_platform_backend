package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.bodyType.dtos.CarBodyTypeResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface BodyTypeMapper {

    @Named("toDto")
    CarBodyTypeResponse toDto(BodyType bodyType);
}

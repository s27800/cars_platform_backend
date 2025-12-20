package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.bodyType.dtos.CarsListBodyTypeResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ICarsListBodyTypeMapper {
    ICarsListBodyTypeMapper INSTANCE = Mappers.getMapper(ICarsListBodyTypeMapper.class);

    @Named("toDto")
    CarsListBodyTypeResponse toDto(BodyType bodyType);
}

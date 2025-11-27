package com.carsplatform.backend.api.outsideDimensions;

import com.carsplatform.backend.api.outsideDimensions.dtos.CarOutsideDimensionsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OutsideDimensionsMapper {
    OutsideDimensionsMapper INSTANCE = Mappers.getMapper(OutsideDimensionsMapper.class);
    CarOutsideDimensionsResponse toOutsideDimensionsDto(OutsideDimensions outsideDimensions);
}

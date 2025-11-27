package com.carsplatform.backend.api.insideDimensions;

import com.carsplatform.backend.api.insideDimensions.dtos.CarInsideDimensionsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InsideDimensionsMapper {
    InsideDimensionsMapper INSTANCE = Mappers.getMapper(InsideDimensionsMapper.class);
    CarInsideDimensionsResponse toInsideDimensionsDto(InsideDimensions insideDimensions);
}

package com.carsplatform.backend.api.insideDimensions;

import com.carsplatform.backend.api.insideDimensions.dtos.CarInsideDimensionsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IInsideDimensionsMapper {
    IInsideDimensionsMapper INSTANCE = Mappers.getMapper(IInsideDimensionsMapper.class);

    @Named("toDto")
    CarInsideDimensionsResponse toDto(InsideDimensions insideDimensions);
}

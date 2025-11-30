package com.carsplatform.backend.api.outsideDimensions;

import com.carsplatform.backend.api.outsideDimensions.dtos.CarOutsideDimensionsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IOutsideDimensionsMapper {
    IOutsideDimensionsMapper INSTANCE = Mappers.getMapper(IOutsideDimensionsMapper.class);

    @Named("toDto")
    CarOutsideDimensionsResponse toDto(OutsideDimensions outsideDimensions);
}

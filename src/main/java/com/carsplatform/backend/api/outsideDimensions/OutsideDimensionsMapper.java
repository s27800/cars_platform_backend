package com.carsplatform.backend.api.outsideDimensions;

import com.carsplatform.backend.api.outsideDimensions.dtos.CarOutsideDimensionsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface OutsideDimensionsMapper {

    @Named("toDto")
    CarOutsideDimensionsResponse toDto(OutsideDimensions outsideDimensions);
}

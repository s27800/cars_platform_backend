package com.carsplatform.backend.api.insideDimensions;

import com.carsplatform.backend.api.insideDimensions.dtos.CarInsideDimensionsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface InsideDimensionsMapper {

    @Named("toDto")
    CarInsideDimensionsResponse toDto(InsideDimensions insideDimensions);
}

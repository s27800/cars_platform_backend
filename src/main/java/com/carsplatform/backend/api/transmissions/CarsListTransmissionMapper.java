package com.carsplatform.backend.api.transmissions;

import com.carsplatform.backend.api.transmissions.dtos.CarsListTransmissionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface CarsListTransmissionMapper {

    @Named("toDto")
    CarsListTransmissionResponse toDto(Transmission transmission);
}

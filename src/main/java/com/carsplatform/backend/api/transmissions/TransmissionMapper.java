package com.carsplatform.backend.api.transmissions;

import com.carsplatform.backend.api.transmissions.dtos.CarTransmissionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface TransmissionMapper {

    @Named("toDto")
    CarTransmissionResponse toDto(Transmission transmission);
}

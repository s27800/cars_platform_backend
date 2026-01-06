package com.carsplatform.backend.api.transmissions;

import com.carsplatform.backend.api.transmissions.dtos.CarsListTransmissionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CarsListTransmissionMapper {
    CarsListTransmissionMapper INSTANCE = Mappers.getMapper(CarsListTransmissionMapper.class);

    @Named("toDto")
    CarsListTransmissionResponse toDto(Transmission transmission);
}

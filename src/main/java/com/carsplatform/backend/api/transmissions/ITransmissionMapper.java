package com.carsplatform.backend.api.transmissions;

import com.carsplatform.backend.api.transmissions.dtos.CarTransmissionResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ITransmissionMapper {
    ITransmissionMapper INSTANCE = Mappers.getMapper(ITransmissionMapper.class);

    @Named("toDto")
    CarTransmissionResponse toDto(Transmission transmission);
}

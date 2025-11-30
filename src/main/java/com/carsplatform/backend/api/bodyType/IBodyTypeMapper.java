package com.carsplatform.backend.api.bodyType;

import com.carsplatform.backend.api.bodyType.dtos.CarBodyTypeResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IBodyTypeMapper {
    IBodyTypeMapper INSTANCE = Mappers.getMapper(IBodyTypeMapper.class);

    @Named("toDto")
    CarBodyTypeResponse toDto(BodyType bodyType);
}

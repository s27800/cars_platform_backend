package com.carsplatform.backend.api.tags;

import com.carsplatform.backend.api.tags.dtos.CarTagResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);
    CarTagResponse toTagDto(Tag tag);
}

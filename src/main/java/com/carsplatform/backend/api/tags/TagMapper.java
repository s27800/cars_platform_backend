package com.carsplatform.backend.api.tags;

import com.carsplatform.backend.api.tags.dtos.CarTagResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    CarTagResponse toDto(Tag tag);

    default List<CarTagResponse> toDtoList(List<Tag> tags) {
        if (tags == null)
            return null;

        return tags.stream().map(this::toDto).collect(Collectors.toList());
    }
}

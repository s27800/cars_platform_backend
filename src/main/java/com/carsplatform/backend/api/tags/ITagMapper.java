package com.carsplatform.backend.api.tags;

import com.carsplatform.backend.api.tags.dtos.CarTagResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ITagMapper {
    ITagMapper INSTANCE = Mappers.getMapper(ITagMapper.class);

    CarTagResponse toDto(Tag tag);

    @Named("toDtoList")
    default Set<CarTagResponse> toDtoList(Set<Tag> tags) {
        if (tags == null)
            return null;

        return tags.stream().map(this::toDto).collect(Collectors.toSet());
    }
}

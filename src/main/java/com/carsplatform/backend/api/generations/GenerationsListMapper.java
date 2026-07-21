package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.generations.dtos.GenerationsListResponse;
import com.carsplatform.backend.api.models.Model;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GenerationsListMapper {
    GenerationsListMapper INSTANCE = Mappers.getMapper(GenerationsListMapper.class);

    default GenerationsListResponse toDto(Generation generation) {
        if (generation == null)
            return null;

        return GenerationsListResponse.builder()
                .id(generation.getId())
                .name(generation.getName())
                .carsCount(generation.getCars() != null ? generation.getCars().size() : 0)
                .build();
    }

    @Named("map")
    default List<GenerationsListResponse> map(Model model) {
        if (model == null)
            return null;

        return model.getGenerations().stream().map(this::toDto).collect(Collectors.toList());
    }
}

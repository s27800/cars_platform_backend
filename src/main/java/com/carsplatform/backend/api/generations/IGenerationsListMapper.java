package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.generations.dtos.GenerationsListResponse;
import com.carsplatform.backend.api.models.Model;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IGenerationsListMapper {
    IGenerationsListMapper INSTANCE = Mappers.getMapper(IGenerationsListMapper.class);

    GenerationsListResponse toDto(Generation generation);

    @Named("map")
    default List<GenerationsListResponse> map(Model model) {
        if (model == null)
            return null;

        return model.getGenerations().stream().map(this::toDto).collect(Collectors.toList());
    }
}

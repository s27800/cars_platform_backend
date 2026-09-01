package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.models.dtos.ModelsListResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface ModelsListMapper {

    default ModelsListResponse toDto(Model model) {
        if (model == null)
            return null;

        return ModelsListResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .generationsCount(model.getGenerations() != null ? model.getGenerations().size() : 0)
                .build();
    }

    @Named("map")
    default List<ModelsListResponse> map(Brand brand) {
        if (brand == null || brand.getModels() == null)
            return Collections.emptyList();

        return brand.getModels().stream().map(this::toDto).collect(Collectors.toList());
    }
}

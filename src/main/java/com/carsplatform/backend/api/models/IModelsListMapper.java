package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.models.dtos.ModelsListResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface IModelsListMapper {
    IModelsListMapper INSTANCE = Mappers.getMapper(IModelsListMapper.class);

    ModelsListResponse toDto(Model model);

    @Named("map")
    default List<ModelsListResponse> map(Brand brand) {
        if (brand == null)
            return null;

        return brand.getModels().stream().map(this::toDto).collect(Collectors.toList());
    }
}

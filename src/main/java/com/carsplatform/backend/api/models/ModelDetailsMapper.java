package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;
import com.carsplatform.backend.api.models.dtos.ModelDetailsResponse;
import com.carsplatform.backend.api.generations.GenerationsListMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = {GenerationsListMapper.class})
public interface ModelDetailsMapper {

    @Mapping(target = "generations", qualifiedByName = "map", source = "model")
    @Mapping(target = "brand", source = "brand", qualifiedByName = "mapBrand")
    ModelDetailsResponse toDto(Model model);

    @Named("mapBrand")
    default BrandsListResponse mapBrand(Brand brand) {
        if (brand == null)
            return null;

        return BrandsListResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logoUrl(brand.getLogoUrl())
                .build();
    }
}

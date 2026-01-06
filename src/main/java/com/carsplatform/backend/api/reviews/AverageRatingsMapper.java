package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface AverageRatingsMapper {
    AverageRatingsMapper INSTANCE = Mappers.getMapper(AverageRatingsMapper.class);

    AverageRatingsResponse toDto(Map<String, Double> ratings);
}

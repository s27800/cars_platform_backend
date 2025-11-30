package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface IAverageRatingsMapper {
    IAverageRatingsMapper INSTANCE = Mappers.getMapper(IAverageRatingsMapper.class);

    AverageRatingsResponse toDto(Map<String, Double> ratings);
}

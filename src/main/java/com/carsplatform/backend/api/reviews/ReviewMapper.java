package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.CarReviewResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);
    CarReviewResponse toReviewDto(Review review);
}

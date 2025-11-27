package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.CarReviewResponse;
import com.carsplatform.backend.api.users.UsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UsernameMapper.class})
public interface ReviewMapper {
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(target = "usernameResponse", source = "user")
    CarReviewResponse toDto(Review review);

    default List<CarReviewResponse> toDtoList(List<Review> reviews) {
        if (reviews == null)
            return null;

        return reviews.stream().map(this::toDto).collect(Collectors.toList());
    }
}

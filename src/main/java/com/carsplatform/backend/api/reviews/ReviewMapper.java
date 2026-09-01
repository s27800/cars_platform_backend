package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.ReviewResponse;
import com.carsplatform.backend.api.users.UsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import org.springframework.data.domain.Page;


@Mapper(componentModel = "spring", uses = {UsernameMapper.class})
public interface ReviewMapper {

    @Mapping(target = "usernameResponse", source = "user")
    @Mapping(target = "likesCount", source = "likesCount")
    ReviewResponse toDto(Review review);

    @Named("toDtoList")
    default Page<ReviewResponse> toDtoList(Page<Review> reviews) {
        if (reviews == null)
            return null;

        return reviews.map(this::toDto);
    }
}

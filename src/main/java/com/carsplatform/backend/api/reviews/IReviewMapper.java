package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.ReviewResponse;
import com.carsplatform.backend.api.users.IUsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", uses = {IUsernameMapper.class})
public interface IReviewMapper {
    IReviewMapper INSTANCE = Mappers.getMapper(IReviewMapper.class);

    @Mapping(target = "usernameResponse", source = "user")
    ReviewResponse toDto(Review review);

    @Named("toDtoList")
    default Page<ReviewResponse> toDtoList(Page<Review> reviews) {
        if (reviews == null)
            return null;

        return reviews.map(this::toDto);
    }
}

package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.cars.CarInfoMapper;
import com.carsplatform.backend.api.reviews.dtos.ReviewDetailsResponse;
import com.carsplatform.backend.api.users.UsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import org.springframework.data.domain.Page;


@Mapper(componentModel = "spring", uses = {UsernameMapper.class, CarInfoMapper.class})
public interface ReviewDetailsMapper {

    @Mapping(target = "usernameResponse", source = "user")
    @Mapping(target = "likesCount", source = "likesCount")
    @Mapping(target = "carInfo", source = "car", qualifiedByName = "toCarInfo")
    ReviewDetailsResponse toDto(Review review);

    @Named("toDtoList")
    default Page<ReviewDetailsResponse> toDtoList(Page<Review> reviews) {
        if (reviews == null)
            return null;

        return reviews.map(this::toDto);
    }
}

package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.CreateReviewRequest;
import com.carsplatform.backend.api.users.UsernameMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UsernameMapper.class})
public interface CreateReviewMapper {
    CreateReviewMapper INSTANCE = Mappers.getMapper(CreateReviewMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "isApproved", constant = "false")
    @Mapping(target = "reviewDate", expression = "java(java.time.LocalDateTime.now())")
    Review toDto(CreateReviewRequest reviewRequest);
}

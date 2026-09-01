package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.CreateReviewRequest;
import com.carsplatform.backend.api.users.UsernameMapper;
import com.carsplatform.backend.common.ModerationStatus;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(
    componentModel = "spring",
    uses = {UsernameMapper.class},
    imports = {ModerationStatus.class}
)
public interface CreateReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "status", expression = "java(ModerationStatus.PENDING)")
    @Mapping(target = "reviewDate", expression = "java(java.time.LocalDateTime.now())")
    Review toEntity(CreateReviewRequest reviewRequest);
}

package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.api.users.dtos.UserResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "isAdmin", ignore = true)
    @Mapping(target = "userSettings", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "fuelReports", ignore = true)
    @Mapping(target = "reviewLikes", ignore = true)
    @Mapping(target = "proposals", ignore = true)
    void updateEntityFromDto(UserModifyRequest userModifyRequest, @MappingTarget User user);
}

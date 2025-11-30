package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UserModifyRequest;
import com.carsplatform.backend.api.users.dtos.UserResponse;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IUserMapper {
    IUserMapper INSTANCE = Mappers.getMapper(IUserMapper.class);
    UserResponse toResponseDto(User user);
    void updateEntityFromDto(UserModifyRequest userModifyRequest, @MappingTarget User user);
}

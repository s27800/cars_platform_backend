package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UsernameResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsernameMapper {
    UsernameMapper INSTANCE = Mappers.getMapper(UsernameMapper.class);
    UsernameResponse toUsernameDto(User user);
}

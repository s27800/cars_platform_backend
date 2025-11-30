package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UsernameResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IUsernameMapper {
    IUsernameMapper INSTANCE = Mappers.getMapper(IUsernameMapper.class);
    UsernameResponse toDto(User user);
}

package com.carsplatform.backend.api.users;

import com.carsplatform.backend.api.users.dtos.UsernameResponse;

import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UsernameMapper {
    UsernameResponse toDto(User user);
}

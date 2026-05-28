package com.carsplatform.backend.api.userSettings;

import com.carsplatform.backend.api.userSettings.dtos.UserSettingsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserSettingsMapper {
    UserSettingsMapper INSTANCE = Mappers.getMapper(UserSettingsMapper.class);

    UserSettingsResponse toDto(UserSettings userSettings);
}

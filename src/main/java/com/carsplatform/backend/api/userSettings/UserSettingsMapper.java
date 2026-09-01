package com.carsplatform.backend.api.userSettings;

import com.carsplatform.backend.api.userSettings.dtos.UserSettingsResponse;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserSettingsMapper {

    UserSettingsResponse toDto(UserSettings userSettings);
}

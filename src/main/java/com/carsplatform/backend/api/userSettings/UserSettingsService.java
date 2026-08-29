package com.carsplatform.backend.api.userSettings;

import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserService;
import com.carsplatform.backend.api.userSettings.dtos.UpdateUserSettingsRequest;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public UserSettings getCurrentUserSettings() {
        User currentUser = userService.getCurrentUser();

        return userSettingsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("UserSettings", "userId", currentUser.getId()));
    }

    @Transactional
    public UserSettings updateCurrentUserSettings(UpdateUserSettingsRequest request) {
        User currentUser = userService.getCurrentUser();

        UserSettings settings = userSettingsRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("UserSettings", "userId", currentUser.getId()));

        if (request.getTheme() != null)
            settings.setTheme(request.getTheme());

        if (request.getLanguage() != null)
            settings.setLanguage(request.getLanguage());

        return userSettingsRepository.save(settings);
    }
}

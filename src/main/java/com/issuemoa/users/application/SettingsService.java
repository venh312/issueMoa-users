package com.issuemoa.users.application;

import com.issuemoa.users.domain.settings.Settings;
import com.issuemoa.users.domain.settings.SettingsRepository;
import com.issuemoa.users.presentation.dto.settings.SettingsRequest;
import com.issuemoa.users.presentation.dto.settings.SettingsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsResponse save(SettingsRequest request) {
        Settings settings = settingsRepository.save(request.toEntity());
        return SettingsResponse.toDto(settings);
    }

    public SettingsResponse findByUserId(Long userId) {
        return SettingsResponse.toDto(settingsRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("[User Settings] Not Found, USER_ID : " + userId)));
    }

    @Transactional
    public SettingsResponse updateSettings(SettingsRequest request) {
        Settings settings = settingsRepository.findByUserId(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("[User Settings] Not Found, USER_ID : " + request.userId()));

        if (request.theme() != null) {
            settings.updateTheme(request.theme());
        }
        if (request.voice() != null) {
            settings.updateVoice(request.voice());
        }
        if (request.speed() != null) {
            settings.updateSpeed(request.speed());
        }
        if (request.language() != null) {
            settings.updateLanguage(request.language());
        }

        return SettingsResponse.toDto(settings);
    }
}

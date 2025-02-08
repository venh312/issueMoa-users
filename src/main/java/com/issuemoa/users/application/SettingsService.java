package com.issuemoa.users.application;

import com.issuemoa.users.domain.settings.Settings;
import com.issuemoa.users.domain.settings.SettingsRepository;
import com.issuemoa.users.presentation.dto.settings.SettingsResponse;
import com.issuemoa.users.presentation.dto.settings.SettingsSaveRequest;
import com.issuemoa.users.presentation.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SettingsService {
    private final SettingsRepository settingsRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public SettingsResponse save(SettingsSaveRequest request) {
        Optional<Settings> findByUserId = settingsRepository.findByUserId(request.userId());

        if (findByUserId.isPresent()) {
            Settings settings = findByUserId.get();
            if (request.theme() != null)
                settings.updateTheme(request.theme());
            if (request.voice() != null)
                settings.updateVoice(request.voice());
            if (request.speed() != null)
                settings.updateSpeed(request.speed());
            if (request.language() != null)
                settings.updateLanguage(request.language());

            return SettingsResponse.toDto(settings);
        }

        Settings settings = settingsRepository.save(request.toEntity());
        return SettingsResponse.toDto(settings);
    }

    public SettingsResponse findByUserId(HttpServletRequest request) {
        Long userId = tokenProvider.getUserId(request);
        return SettingsResponse.toDto(
                settingsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("[USER_ID: "+userId+" 사용자의 설정 정보가 없습니다.]"))
        );
    }
}

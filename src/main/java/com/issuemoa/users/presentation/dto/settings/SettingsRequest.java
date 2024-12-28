package com.issuemoa.users.presentation.dto.settings;

import com.issuemoa.users.domain.settings.Settings;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Users Settings Request")
public record SettingsRequest(
        @Schema(description = "USER ID") Long userId,
        @Schema(description = "[W or D] White, Dark") String theme,
        @Schema(description = "[W or M] Woman, Man]") String voice,
        @Schema(description = "[S or N or F] Slow, Normal, Fast") String speed,
        @Schema(description = "[KR or EN] Korea, English") String language) {

    public Settings toEntity() {
        return Settings.builder()
                .userId(userId)
                .theme(theme)
                .voice(voice)
                .speed(speed)
                .language(language)
                .build();
    }
}

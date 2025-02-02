package com.issuemoa.users.presentation.dto.settings;

import com.issuemoa.users.domain.settings.Settings;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(name = "사용자 설정 저장 요청")
public record SettingsSaveRequest(
        @Schema(description = "사용자 ID", example = "7")
        Long userId,
        @Schema(description = "W: 화이트, D: 다크", example = "W") String theme,
        @Schema(description = "M: 남성, W: 여성]", example = "M") String voice,
        @Schema(description = "S: 천천히, N: 보통, F: 빠르게", example = "N") String speed,
        @Schema(description = "EN: 영어, KO: 한국어", example = "EN") String language) {

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

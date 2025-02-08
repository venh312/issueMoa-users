package com.issuemoa.users.presentation.controller;

import com.issuemoa.users.application.SettingsService;
import com.issuemoa.users.presentation.dto.settings.SettingsResponse;
import com.issuemoa.users.presentation.dto.settings.SettingsSaveRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "Settings API", description = "사용자 환경 설정 API")
@RequiredArgsConstructor
@RestController
public class SettingsController {
    private final SettingsService settingsService;

    @Operation(
            summary = "사용자 설정 정보 저장/변경",
            description = "사용자 설정 정보를 저장하거나, 이미 존재하는 경우 정보를 변경합니다.")
    @PostMapping("/settings")
    public ResponseEntity<SettingsResponse> save(@RequestBody SettingsSaveRequest request) {
        return ResponseEntity.ok(settingsService.save(request));
    }

    @Operation(
            summary = "사용자 설정 정보 조회",
            description = "사용자 ID(userId)를 입력 받아 설정 정보를 반환합니다.")
    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> findByUserId(HttpServletRequest request) {
        return ResponseEntity.ok(settingsService.findByUserId(request));
    }
}

package com.issuemoa.users.presentation.controller;

import com.issuemoa.users.application.SettingsService;
import com.issuemoa.users.presentation.dto.settings.SettingsRequest;
import com.issuemoa.users.presentation.dto.settings.SettingsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Settings", description = "Settings API (사용자 설정 API)")
@RequiredArgsConstructor
@RestController
public class SettingsController {
    private final SettingsService settingsService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = SettingsResponse.class)))})
    @Operation(summary = "Setting Save", description = "사용자 설정 정보를 저장한다.")
    @PostMapping("/settings")
    public ResponseEntity<SettingsResponse> save(@RequestBody SettingsRequest request) {
        return ResponseEntity.ok(settingsService.save(request));
    }

    @Operation(summary = "Setting Save", description = "사용자 설정 정보를 조회한다.")
    @GetMapping("/settings/{userId}")
    public ResponseEntity<SettingsResponse> findByUserId(
            @Parameter(description = "USER ID") @PathVariable("userId") Long userId
    ) {
        return ResponseEntity.ok(settingsService.findByUserId(userId));
    }

    @Operation(summary = "Setting Update", description = "사용자 설정 정보를 변경한다.")
    @PutMapping("/settings")
    public ResponseEntity<SettingsResponse> updateSettings(@RequestBody SettingsRequest request) {
        return ResponseEntity.ok(settingsService.updateSettings(request));
    }
}

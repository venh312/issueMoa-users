package com.issuemoa.users.presentation.controller;

import com.issuemoa.users.application.UsersService;
import com.issuemoa.users.domain.users.Users;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Tag(name = "Users API", description = "로그인 및 사용자 정보 조회")
@RequiredArgsConstructor
@RestController
public class UsersController {
    private final UsersService usersService;

    @Operation(
            summary = "Users Info",
            description = "사용자 정보를 반환합니다.")
    @GetMapping("/users")
    public ResponseEntity<Users> getUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(usersService.getUserInfo(request));
    }

    @Operation(
            summary = "Users Reissue",
            description = "리프레시 토큰으로 액세스 토큰을 재생성합니다.")
    @PostMapping("/users/reissue")
    public ResponseEntity<HashMap<String, Object>> reissue(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(usersService.reissue(request, response));
    }

    @Operation(
            summary = "signOut",
            description = "로그아웃 처리를 합니다.")
    @GetMapping("/users/signOut")
    public ResponseEntity<Boolean> signOut(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(usersService.signOut(request, response));
    }
}

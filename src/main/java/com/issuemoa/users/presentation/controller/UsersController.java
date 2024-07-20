package com.issuemoa.users.presentation.controller;

import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.application.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Tag(name = "Users", description = "Users API (로그인 및 사용자 정보 조회")
@RequiredArgsConstructor
@RestController
public class UsersController {
    private final UsersService usersService;

    @Operation(summary = "Users Info", description = "사용자 정보를 반환한다. <br>Headers Authorization에 [Bearer 토큰 값] 형식으로 전달한다.")
    @GetMapping("/users")
    public ResponseEntity<Users> getUserInfo(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(usersService.getUserInfo(token.replace("Bearer ", "")));
    }

    @Operation(summary = "Users Reissue", description = "refreshToken 으로 액세스 토큰을 재발급한다.")
    @PostMapping("/users/reissue")
    public ResponseEntity<HashMap<String, Object>> reissue(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(usersService.reissue(request, response));
    }

    @Operation(summary = "signOut", description = "로그아웃 처리를 한다.")
    @GetMapping("/users/signOut")
    public ResponseEntity<Boolean> signOut(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(usersService.signOut(request, response));
    }
}

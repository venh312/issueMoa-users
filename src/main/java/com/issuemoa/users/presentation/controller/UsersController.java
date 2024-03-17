package com.issuemoa.users.presentation.controller;

import com.issuemoa.users.infrastructure.common.LoginComponent;
import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.presentation.dto.UsersSignInRequest;
import com.issuemoa.users.application.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Tag(name = "Users", description = "Users API (로그인 및 사용자 정보 조회")
@RequiredArgsConstructor
@Controller
public class UsersController {
    private final UsersService usersService;
    private final LoginComponent loginComponent;

//    @Value("${api.endpoint.recaptchaSiteVerify}")
//    private String recaptchaSiteVerifyEndpoint;
//
//    @Value("${api.secret.recaptcha}")
//    private String secretRecaptcha;

    @Operation(summary = "Users SignIn", description = "사용자 로그인 / 회원가입")
    @PostMapping("/users/signIn")
    public ResponseEntity<HashMap<String, Object>> signIn(@RequestBody UsersSignInRequest request, HttpServletResponse response) {
        Users users = usersService.findByUid(request);

        // 존재 하지 않는 사용자는 등록 한다.
        if (users == null) users = usersService.save(request);

        return ResponseEntity.ok(loginComponent.onSuccess(users, response));

//        String url = recaptchaSiteVerifyEndpoint + "?secret=" + secretRecaptcha + "&response=" + request.getRecaptchaValue();
//        HashMap<String, Object> recaptchaMap = new HttpApiUtil().getDataFromJson(
//                url, "UTF-8", "post", "", "application/x-www-form-urlencoded");
//        boolean recaptchaResult = (boolean) recaptchaMap.get("success");
//        Long resultSave = 0L;
//
//        if (recaptchaResult) {
//            resultSave = usersService.save(request);
//        }
   }
    @Operation(summary = "Users SignIn Oauth2 Google", description = "구글 사용자 로그인 / 회원가입")
    @PostMapping("/users/signIn/oauth2/code/google")
    public ResponseEntity<HashMap<String, Object>> signInOauthCodeGoogle() {
        return null;
    }

    @Operation(summary = "Users Reissue", description = "refreshToken 쿠키 값을 검증하여 재발급한다.")
    @PostMapping("/users/reissue")
    public ResponseEntity<HashMap<String, Object>> reissue(
            @Parameter(description = "[Headers] AUTHORIZATION") HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(usersService.reissue(request, response));
    }

    @Operation(summary = "Users Info", description = "사용자 정보를 반환한다. <br>Headers Authorization에 [Bearer 토큰 값] 형식으로 전달한다.")
    @GetMapping("/users/info")
    public ResponseEntity<Users> getUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(usersService.getUserInfo(request));
    }

    @Operation(summary = "signOut", description = "로그아웃 처리를 한다.")
    @GetMapping("/users/signOut")
    public ResponseEntity<Boolean> signOut(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(usersService.signOut(request, response));
    }

    @Operation(summary = "User Info", description = "사용자 정보 조회.")
    @GetMapping("/users/test")
    public ResponseEntity<Users> userInfo(@RequestParam("uid") String uid) {
        return ResponseEntity.ok(usersService.selectUserInfo(uid));
    }
}

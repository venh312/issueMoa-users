package com.issuemoa.users.web;

import com.issuemoa.users.common.LoginComponent;
import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.message.RestMessage;
import com.issuemoa.users.service.UsersSignInRequest;
import com.issuemoa.users.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Tag(name = "Users", description = "Users API")
@RequiredArgsConstructor
@Controller
public class UsersController {
    private final UsersService usersService;
    private final LoginComponent loginComponent;

    @Value("${api.endpoint.recaptchaSiteVerify}")
    private String recaptchaSiteVerifyEndpoint;
    @Value("${api.secret.recaptcha}")
    private String secretRecaptcha;

    @Operation(summary = "Users SignIn", description = "사용자 로그인 / 회원가입")
    @PostMapping("/users/signIn")
    public ResponseEntity<RestMessage> signIn(@RequestBody UsersSignInRequest request, HttpServletResponse response) {
        HashMap<String, Object> result = new HashMap<>();

        boolean isExists = true;

        Users user = usersService.findByUid(request);

        // 존재 하지 않는 사용자는 User로 등록 한다.
        if (user == null) {
            long id = usersService.save(request);
            if (id > 0)
                user = Users.builder()
                            .uid(request.uid())
                            .name(request.name())
                            .email(request.email())
                            .snsType(request.snsType())
                            .build();
            else
                isExists = false;
        }

        if (isExists)
            result = loginComponent.onSuccess(user, response);

//        String url = recaptchaSiteVerifyEndpoint + "?secret=" + secretRecaptcha + "&response=" + request.getRecaptchaValue();
//        HashMap<String, Object> recaptchaMap = new HttpApiUtil().getDataFromJson(
//                url, "UTF-8", "post", "", "application/x-www-form-urlencoded");
//        boolean recaptchaResult = (boolean) recaptchaMap.get("success");
//        Long resultSave = 0L;
//
//        if (recaptchaResult) {
//            resultSave = usersService.save(request);
//        }
//
        return ResponseEntity.ok()
            .headers(new HttpHeaders())
            .body(new RestMessage(HttpStatus.OK, result));
   }

    @Operation(summary = "Users Reissue", description = "refreshToken 쿠키 값을 검증하여 재발급한다.")
    @PostMapping("/users/reissue")
    public ResponseEntity<RestMessage> reissue(
            @Parameter(description = "HttpHeaders.AUTHORIZATION") HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .body(new RestMessage(HttpStatus.OK, usersService.reissue(request, response)));
    }

    @Operation(summary = "Users Info", description = "사용자 정보를 반환한다. <br>Headers Authorization에 [Bearer 토큰] 형식으로 전달한다.")
    @GetMapping("/users/info")
    public ResponseEntity<RestMessage> getUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .body(new RestMessage(HttpStatus.OK, usersService.getUserInfo(request)));
    }

    @Operation(summary = "signOut", description = "로그아웃 처리를 한다.")
    @GetMapping("/users/signOut")
    public ResponseEntity<RestMessage> signOut(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .body(new RestMessage(HttpStatus.OK, usersService.signOut(request, response)));
    }

    @Operation(summary = "User Info", description = "사용자 정보 조회.")
    @GetMapping("/users/test")
    public ResponseEntity<RestMessage> userInfo(@RequestParam("uid") String uid) {
        return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .body(new RestMessage(HttpStatus.OK, usersService.selectUserInfo(uid)));
    }
}

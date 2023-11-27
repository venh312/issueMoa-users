package com.issuemoa.users.web;

import com.issuemoa.users.common.LoginComponent;
import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.message.RestMessage;
import com.issuemoa.users.service.UsersService;
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

@RequiredArgsConstructor
@Controller
public class UsersController {
    private final UsersService usersService;
    private final LoginComponent loginComponent;
    @Value("${api.endpoint.bookmarkFindByUserId}")
    private String bookmarkFindByUserIdEndpoint;
    @Value("${api.endpoint.recaptchaSiteVerify}")
    private String recaptchaSiteVerifyEndpoint;
    @Value("${api.secret.recaptcha}")
    private String secretRecaptcha;

    @PostMapping("/users/signIn")
    public ResponseEntity<RestMessage> signIn(@RequestBody Users.Request request) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        boolean isExists = true;

        Users user = usersService.findByUid(request);
        if (user == null) {
            long id = usersService.save(request);
            if (id > 0)
                user = usersService.findById(id);
            else
                isExists = false;
        }

        if (isExists)
            result = loginComponent.onSuccess(user);

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

    @PostMapping("/reissue")
    public ResponseEntity<RestMessage> reissue(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.reissue(request, response)));
    }
}

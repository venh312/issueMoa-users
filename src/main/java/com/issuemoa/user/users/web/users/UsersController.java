package com.issuemoa.user.users.web.users;

import com.issuemoa.user.users.common.HttpApiUtil;
import com.issuemoa.user.users.common.RequestUtil;
import com.issuemoa.user.users.domain.users.Users;
import com.issuemoa.user.users.message.RestMessage;
import com.issuemoa.user.users.service.users.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UsersController {
    private final UsersService usersService;
    @Value("${api.endpoint.bookmarkFindByUserId}")
    private String bookmarkFindByUserIdEndpoint;
    @Value("${api.endpoint.recaptchaSiteVerify}")
    private String recaptchaSiteVerifyEndpoint;
    @Value("${api.secret.recaptcha}")
    private String secretRecaptcha;

    @PostMapping("/save")
    public ResponseEntity<RestMessage> save(Users.Request request) throws Exception{
        String url = recaptchaSiteVerifyEndpoint + "?secret=" + secretRecaptcha + "&response=" + request.getRecaptchaValue();
        HashMap<String, Object> recaptchaMap = new HttpApiUtil().getDataFromJson(
                url, "UTF-8", "post", "", "application/x-www-form-urlencoded");
        boolean recaptchaResult = (boolean) recaptchaMap.get("success");
        Long resultSave = 0L;

        if (recaptchaResult) {
            resultSave = usersService.save(request);
        }

        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, resultSave));
    }

    @PostMapping("/count-by/email")
    public ResponseEntity<RestMessage> countByEmailAndType(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.countByEmailAndType(request.getEmail(), request.getType())));
    }

    @PostMapping("/my-page/index")
    public ResponseEntity<RestMessage> getMyPageIndex() {
        RestTemplate restTemplate = new RestTemplate();
        HashMap<Object, Object> resultMap = new HashMap<>();

        resultMap.put("userInfo", usersService.findById(RequestUtil.getUserId()));
        resultMap.put("bookmarkList", restTemplate.getForObject(bookmarkFindByUserIdEndpoint + "?userId=" + RequestUtil.getUserId(), Object.class));

        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, resultMap));
    }

    @PostMapping("/my-page/detail")
    public ResponseEntity<RestMessage> findById(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.findById(request.getId())));
    }

    @PostMapping("/my-page/update-info")
    public ResponseEntity<RestMessage> updateUsersInfo(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.updateUsersInfo(request)));
    }

    @PostMapping("/my-page/update-password")
    public ResponseEntity<RestMessage> updatePassword(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.updatePassword(request)));
    }

    @PostMapping("/my-page/update-drop")
    public ResponseEntity<RestMessage> updateDropYn(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.updateDropYn(request)));
    }

    @PostMapping("/my-page/update-name")
    public ResponseEntity<RestMessage> updateName(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.updateName(request)));
    }

    @PostMapping("/my-page/update-address")
    public ResponseEntity<RestMessage> updateAddress(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.updateAddress(request)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<RestMessage> reissue(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.reissue(request, response)));
    }
}

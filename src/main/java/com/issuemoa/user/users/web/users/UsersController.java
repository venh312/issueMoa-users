package com.issuemoa.user.users.web.users;

import com.issuemoa.user.users.domain.users.Users;
import com.issuemoa.user.users.message.RestMessage;
import com.issuemoa.user.users.service.users.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UsersController {

    private final UsersService usersService;

    @PostMapping("/save")
    public ResponseEntity<RestMessage> save(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.save(request)));
    }

    @GetMapping("/count-by/email")
    public ResponseEntity<RestMessage> findById(@RequestParam String email) throws Exception {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.countByEmail(email)));
    }

    @GetMapping("/my-page/detail")
    public ResponseEntity<RestMessage> findById(@RequestParam Long id) throws Exception {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.findById(id)));
    }

    @PostMapping("/my-page/update-info")
    public ResponseEntity<RestMessage> updateUsersInfo(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.updateUsersInfo(request)));
    }

    @PostMapping("/my-page/update-password")
    public ResponseEntity<RestMessage> updateUsersPassword(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.updateUsersPassword(request)));
    }

    @PostMapping("/my-page/update-drop")
    public ResponseEntity<RestMessage> updateDropYn(Users.Request request) {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.updateDropYn(request)));
    }
}

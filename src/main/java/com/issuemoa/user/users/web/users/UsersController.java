package com.issuemoa.user.users.web.users;

import com.issuemoa.user.users.message.RestMessage;
import com.issuemoa.user.users.service.users.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UsersController {

    private final UsersService usersService;

    @GetMapping("/detail")
    public ResponseEntity<RestMessage> findById(@RequestParam Long id) throws Exception {
        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, usersService.findById(id)));
    }
}

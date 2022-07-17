package com.issuemoa.user.users.handler;

import com.issuemoa.user.users.domain.users.UsersRepository;
import com.issuemoa.user.users.jwt.TokenProvider;
import com.issuemoa.user.users.message.RestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UsersRepository usersRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        usersRepository.updateLastLoginTime(authentication.getName());

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {

            HashMap<String, Object> tokenMap =  new TokenProvider().generateTokenDto(authentication);
            HashMap<String, Object> resultMap = new HashMap<>();

            resultMap.put("code", "LGN");
            resultMap.put("accessToken", tokenMap.get("accessToken"));
            resultMap.put("accessTokenExpires", tokenMap.get("accessToken"));
            resultMap.put("refreshToken", tokenMap.get("refreshToken"));

            bw.write(resultMap.toString());
        }
    }
}

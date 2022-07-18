package com.issuemoa.user.users.handler;

import com.issuemoa.user.users.domain.users.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final UsersRepository usersRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String msg = "Invalid Email or Password";

        // exception 관련 메세지 처리
        if (exception instanceof DisabledException) {
            msg = "DisabledException account";
        } else if(exception instanceof CredentialsExpiredException) {
            msg = "CredentialsExpiredException account";
        } else if(exception instanceof BadCredentialsException) {
            msg = "BadCredentialsException account";
        }

        usersRepository.updateFailLogin(request.getParameter("email"));

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        HashMap<String, String> resultMap = new HashMap<>();

        resultMap.put("code", "IV_LGN");
        resultMap.put("msg", msg);

        jsonConverter.write(resultMap, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
    }
}

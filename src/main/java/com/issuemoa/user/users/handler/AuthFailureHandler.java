package com.issuemoa.user.users.handler;

import com.issuemoa.user.users.common.LoginComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginComponent loginComponent;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        String msg = "Invalid Email or Password";

        if (exception instanceof DisabledException) {
            msg = "DisabledException account";
        } else if(exception instanceof CredentialsExpiredException) {
            msg = "CredentialsExpiredException account";
        } else if(exception instanceof BadCredentialsException) {
            msg = "BadCredentialsException account";
        }

        loginComponent.onFailure(request.getParameter("email"), msg, response);
    }
}

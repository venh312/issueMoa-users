package com.issuemoa.user.users.interceptor;

import com.issuemoa.user.users.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("==> HandlerInterceptor preHandle.");
        boolean flag = false;
        String token = tokenProvider.resolveToken(request);
        String validMsg = "Empty ValidateToken.";

        if (StringUtils.hasText(token)) {
            HashMap<String,Object> resultMap = tokenProvider.validateToken(token);
            flag = (boolean) resultMap.get("flag");
            if (resultMap.get("validMsg") != null) {
                validMsg = (String) resultMap.get("validMsg");
            }
        }

        new MappingJackson2HttpMessageConverter().write(validMsg, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
        return flag;
    }
}

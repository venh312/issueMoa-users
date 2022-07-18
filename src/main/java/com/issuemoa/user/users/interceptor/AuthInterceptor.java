package com.issuemoa.user.users.interceptor;

import com.issuemoa.user.users.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("==> HandlerInterceptor preHandle.");

        TokenProvider tokenProvider = new TokenProvider();
        String token = tokenProvider.resolveToken(request);

        if (StringUtils.hasText(token)) {
            return tokenProvider.validateToken(token);
        } else {
            MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("code", "refused");
            resultMap.put("msg", "invalid resolveToken");
            jsonConverter.write(resultMap, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));

            return false;
        }
    }
}

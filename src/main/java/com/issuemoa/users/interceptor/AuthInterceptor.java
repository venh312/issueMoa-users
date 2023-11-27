package com.issuemoa.users.interceptor;

import com.issuemoa.users.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
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
//        String token = tokenProvider.resolveToken(request);
//        String validCode = "TK_EM";
//        boolean validFlag = false;
//
//        if (StringUtils.hasText(token)) {
//            HashMap<String,Object> tokenMap = tokenProvider.validateToken(token);
//            validFlag = (boolean) tokenMap.get("flag");
//            validCode = (String) tokenMap.get("code");
//            RequestContextHolder.getRequestAttributes().setAttribute("claims", tokenMap.get("claims"), RequestAttributes.SCOPE_REQUEST);
//        }
//
//        HashMap<String, Object> resultMap = new HashMap<>();
//        resultMap.put("validCode", validCode);
//        resultMap.put("validFlag", validFlag);
//
//        if (!validFlag) {
//            new MappingJackson2HttpMessageConverter().write(resultMap, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
//        }
//
//        return validFlag;
        return true;
    }
}

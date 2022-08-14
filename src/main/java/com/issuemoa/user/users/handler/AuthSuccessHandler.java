package com.issuemoa.user.users.handler;

import com.issuemoa.user.users.common.CookieUtil;
import com.issuemoa.user.users.domain.users.Users;
import com.issuemoa.user.users.domain.users.UsersRepository;
import com.issuemoa.user.users.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UsersRepository usersRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        Users users = (Users) authentication.getPrincipal();

        HashMap<String, Object> tokenMap =  tokenProvider.generateToken(authentication);
        HashMap<String, Object> resultMap = new HashMap<>();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

        usersRepository.updateLastLoginTime(users.getId());

        if (users.getLoginFailCnt() > 4) {
            resultMap.put("code", "LOCK_LGN");
            resultMap.put("msg", "Login failed 5 times");
            jsonConverter.write(resultMap, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
            return;
        }

        if ("Y".equals(users.getDropYn())) {
            resultMap.put("code", "DROP_LGN");
            resultMap.put("msg", "Withdrawal user");
            jsonConverter.write(resultMap, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
            return;
        }

        resultMap.put("code", "LGN");
        resultMap.put("msg", "Success login");
        resultMap.put("accessToken", tokenMap.get("accessToken"));
        resultMap.put("accessTokenExpires", tokenMap.get("accessTokenExpires"));

        String refreshToken = (String) tokenMap.get("refreshToken");
        long refreshExpires = Long.parseLong((String) tokenMap.get("refreshTokenExpires"));

        // Redis Set Data - refreshToken
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        vop.set(refreshToken, authentication.getName(), Duration.ofSeconds(refreshExpires));

        // Cookie Set Data - refreshToken
        response.addCookie(CookieUtil.setRefreshTokenCookie(refreshToken, refreshExpires));

        jsonConverter.write(resultMap, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
    }
}

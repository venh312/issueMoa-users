package com.issuemoa.users.common;

import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.domain.users.UsersRepository;
import com.issuemoa.users.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class LoginComponent {
    private final UsersRepository usersRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenProvider tokenProvider;

    public HashMap<String, Object> onSuccess(Users users, HttpServletResponse response) {
        HashMap<String, Object> tokenMap =  tokenProvider.generateToken(users);
        HashMap<String, Object> resultMap = new HashMap<>();

        usersRepository.updateLastLoginTime(users.getId());

        resultMap.put("msg", "Success");
        resultMap.put("accessToken", tokenMap.get("accessToken"));
        resultMap.put("accessTokenExpires", tokenMap.get("accessTokenExpires"));

        String refreshToken = (String) tokenMap.get("refreshToken");
        long refreshExpires = Long.parseLong((String) tokenMap.get("refreshTokenExpires"));

        // Redis Set Data - refreshToken
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        vop.set(refreshToken, String.valueOf(users.getId()), Duration.ofSeconds(refreshExpires));

        response.addCookie(CookieUtil.setRefreshTokenCookie((String) refreshToken, refreshExpires));

        return resultMap;
    }

}

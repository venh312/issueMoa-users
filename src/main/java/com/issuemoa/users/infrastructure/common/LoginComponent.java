package com.issuemoa.users.infrastructure.common;

import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.domain.users.UsersRepository;
import com.issuemoa.users.presentation.jwt.TokenProvider;
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
        Duration accessTokenDuration = Duration.ofMinutes(30);
        long accessTokenExpires = accessTokenDuration.toSeconds();

        // accessToken 발급
        String accessToken =  tokenProvider.generateToken(users, accessTokenDuration);

        // 로그인 시간 갱신
        usersRepository.updateLastLoginTime(users.getId());

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("msg", "Success");
        resultMap.put("accessToken", accessToken);
        resultMap.put("accessTokenExpires", accessTokenExpires);

        Duration refreshTokenTokenDuration = Duration.ofDays(14);
        long refreshTokenExpires = refreshTokenTokenDuration.toSeconds();

        // refreshToken 토큰 발급
        String refreshToken =  tokenProvider.generateToken(users, refreshTokenTokenDuration);

        // Redis - refreshToken 토큰 저장
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        vop.set(refreshToken, String.valueOf(users.getId()), refreshTokenExpires);

        CookieUtil.addCookie(response, "refreshToken", refreshToken, (int) refreshTokenExpires,true);

        return resultMap;
    }
}
